package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.ICrossbow;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PathfinderGoalCrossbowAttack<T extends EntityMonster & IRangedEntity & ICrossbow> extends PathfinderGoal {

    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeRange.rangeOfSeconds(1, 2);
    private final T mob;
    private PathfinderGoalCrossbowAttack.State crossbowState;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public PathfinderGoalCrossbowAttack(T t0, double d0, float f) {
        this.crossbowState = PathfinderGoalCrossbowAttack.State.UNCHARGED;
        this.mob = t0;
        this.speedModifier = d0;
        this.attackRadiusSqr = f * f;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.isValidTarget() && this.isHoldingCrossbow();
    }

    private boolean isHoldingCrossbow() {
        return this.mob.isHolding(Items.CROSSBOW);
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
    }

    private boolean isValidTarget() {
        return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.setTarget((EntityLiving) null);
        this.seeTime = 0;
        if (this.mob.isUsingItem()) {
            this.mob.stopUsingItem();
            ((ICrossbow) this.mob).setChargingCrossbow(false);
            ItemCrossbow.setCharged(this.mob.getUseItem(), false);
        }

    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        EntityLiving entityliving = this.mob.getTarget();

        if (entityliving != null) {
            boolean flag = this.mob.getSensing().hasLineOfSight(entityliving);
            boolean flag1 = this.seeTime > 0;

            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            double d0 = this.mob.distanceToSqr((Entity) entityliving);
            boolean flag2 = (d0 > (double) this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;

            if (flag2) {
                --this.updatePathDelay;
                if (this.updatePathDelay <= 0) {
                    this.mob.getNavigation().moveTo((Entity) entityliving, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
                    this.updatePathDelay = PathfinderGoalCrossbowAttack.PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
                }
            } else {
                this.updatePathDelay = 0;
                this.mob.getNavigation().stop();
            }

            this.mob.getLookControl().setLookAt(entityliving, 30.0F, 30.0F);
            if (this.crossbowState == PathfinderGoalCrossbowAttack.State.UNCHARGED) {
                if (!flag2) {
                    this.mob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.CHARGING;
                    ((ICrossbow) this.mob).setChargingCrossbow(true);
                }
            } else if (this.crossbowState == PathfinderGoalCrossbowAttack.State.CHARGING) {
                if (!this.mob.isUsingItem()) {
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.UNCHARGED;
                }

                int i = this.mob.getTicksUsingItem();
                ItemStack itemstack = this.mob.getUseItem();

                if (i >= ItemCrossbow.getChargeDuration(itemstack)) {
                    this.mob.releaseUsingItem();
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.CHARGED;
                    this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                    ((ICrossbow) this.mob).setChargingCrossbow(false);
                }
            } else if (this.crossbowState == PathfinderGoalCrossbowAttack.State.CHARGED) {
                --this.attackDelay;
                if (this.attackDelay == 0) {
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.READY_TO_ATTACK;
                }
            } else if (this.crossbowState == PathfinderGoalCrossbowAttack.State.READY_TO_ATTACK && flag) {
                ((IRangedEntity) this.mob).performRangedAttack(entityliving, 1.0F);
                ItemStack itemstack1 = this.mob.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.CROSSBOW));

                ItemCrossbow.setCharged(itemstack1, false);
                this.crossbowState = PathfinderGoalCrossbowAttack.State.UNCHARGED;
            }

        }
    }

    private boolean canRun() {
        return this.crossbowState == PathfinderGoalCrossbowAttack.State.UNCHARGED;
    }

    private static enum State {

        UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;

        private State() {}
    }
}

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

    public static final UniformInt PATHFINDING_DELAY_RANGE = TimeRange.a(1, 2);
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
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        return this.h() && this.g();
    }

    private boolean g() {
        return this.mob.a(Items.CROSSBOW);
    }

    @Override
    public boolean b() {
        return this.h() && (this.a() || !this.mob.getNavigation().m()) && this.g();
    }

    private boolean h() {
        return this.mob.getGoalTarget() != null && this.mob.getGoalTarget().isAlive();
    }

    @Override
    public void d() {
        super.d();
        this.mob.setAggressive(false);
        this.mob.setGoalTarget((EntityLiving) null);
        this.seeTime = 0;
        if (this.mob.isHandRaised()) {
            this.mob.clearActiveItem();
            ((ICrossbow) this.mob).b(false);
            ItemCrossbow.a(this.mob.getActiveItem(), false);
        }

    }

    @Override
    public void e() {
        EntityLiving entityliving = this.mob.getGoalTarget();

        if (entityliving != null) {
            boolean flag = this.mob.getEntitySenses().a(entityliving);
            boolean flag1 = this.seeTime > 0;

            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            double d0 = this.mob.f((Entity) entityliving);
            boolean flag2 = (d0 > (double) this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;

            if (flag2) {
                --this.updatePathDelay;
                if (this.updatePathDelay <= 0) {
                    this.mob.getNavigation().a((Entity) entityliving, this.j() ? this.speedModifier : this.speedModifier * 0.5D);
                    this.updatePathDelay = PathfinderGoalCrossbowAttack.PATHFINDING_DELAY_RANGE.a(this.mob.getRandom());
                }
            } else {
                this.updatePathDelay = 0;
                this.mob.getNavigation().o();
            }

            this.mob.getControllerLook().a(entityliving, 30.0F, 30.0F);
            if (this.crossbowState == PathfinderGoalCrossbowAttack.State.UNCHARGED) {
                if (!flag2) {
                    this.mob.c(ProjectileHelper.a((EntityLiving) this.mob, Items.CROSSBOW));
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.CHARGING;
                    ((ICrossbow) this.mob).b(true);
                }
            } else if (this.crossbowState == PathfinderGoalCrossbowAttack.State.CHARGING) {
                if (!this.mob.isHandRaised()) {
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.UNCHARGED;
                }

                int i = this.mob.eJ();
                ItemStack itemstack = this.mob.getActiveItem();

                if (i >= ItemCrossbow.k(itemstack)) {
                    this.mob.releaseActiveItem();
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.CHARGED;
                    this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                    ((ICrossbow) this.mob).b(false);
                }
            } else if (this.crossbowState == PathfinderGoalCrossbowAttack.State.CHARGED) {
                --this.attackDelay;
                if (this.attackDelay == 0) {
                    this.crossbowState = PathfinderGoalCrossbowAttack.State.READY_TO_ATTACK;
                }
            } else if (this.crossbowState == PathfinderGoalCrossbowAttack.State.READY_TO_ATTACK && flag) {
                ((IRangedEntity) this.mob).a(entityliving, 1.0F);
                ItemStack itemstack1 = this.mob.b(ProjectileHelper.a((EntityLiving) this.mob, Items.CROSSBOW));

                ItemCrossbow.a(itemstack1, false);
                this.crossbowState = PathfinderGoalCrossbowAttack.State.UNCHARGED;
            }

        }
    }

    private boolean j() {
        return this.crossbowState == PathfinderGoalCrossbowAttack.State.UNCHARGED;
    }

    private static enum State {

        UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;

        private State() {}
    }
}

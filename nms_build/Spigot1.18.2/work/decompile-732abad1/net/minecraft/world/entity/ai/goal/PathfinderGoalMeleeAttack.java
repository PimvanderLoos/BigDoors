package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;

public class PathfinderGoalMeleeAttack extends PathfinderGoal {

    protected final EntityCreature mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private PathEntity path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval = 20;
    private long lastCanUseCheck;
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

    public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.followingTargetEvenIfNotSeen = flag;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean canUse() {
        long i = this.mob.level.getGameTime();

        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            EntityLiving entityliving = this.mob.getTarget();

            if (entityliving == null) {
                return false;
            } else if (!entityliving.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().createPath((Entity) entityliving, 0);
                return this.path != null ? true : this.getAttackReachSqr(entityliving) >= this.mob.distanceToSqr(entityliving.getX(), entityliving.getY(), entityliving.getZ());
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        EntityLiving entityliving = this.mob.getTarget();

        return entityliving == null ? false : (!entityliving.isAlive() ? false : (!this.followingTargetEvenIfNotSeen ? !this.mob.getNavigation().isDone() : (!this.mob.isWithinRestriction(entityliving.blockPosition()) ? false : !(entityliving instanceof EntityHuman) || !entityliving.isSpectator() && !((EntityHuman) entityliving).isCreative())));
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        EntityLiving entityliving = this.mob.getTarget();

        if (!IEntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entityliving)) {
            this.mob.setTarget((EntityLiving) null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        EntityLiving entityliving = this.mob.getTarget();

        if (entityliving != null) {
            this.mob.getLookControl().setLookAt(entityliving, 30.0F, 30.0F);
            double d0 = this.mob.distanceToSqr(entityliving.getX(), entityliving.getY(), entityliving.getZ());

            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(entityliving)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || entityliving.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = entityliving.getX();
                this.pathedTargetY = entityliving.getY();
                this.pathedTargetZ = entityliving.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                if (d0 > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo((Entity) entityliving, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(entityliving, d0);
        }
    }

    protected void checkAndPerformAttack(EntityLiving entityliving, double d0) {
        double d1 = this.getAttackReachSqr(entityliving);

        if (d0 <= d1 && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(EnumHand.MAIN_HAND);
            this.mob.doHurtTarget(entityliving);
        }

    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(20);
    }

    protected double getAttackReachSqr(EntityLiving entityliving) {
        return (double) (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + entityliving.getBbWidth());
    }
}

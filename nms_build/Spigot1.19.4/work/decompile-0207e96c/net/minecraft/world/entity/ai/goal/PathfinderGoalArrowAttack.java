package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.IRangedEntity;

public class PathfinderGoalArrowAttack extends PathfinderGoal {

    private final EntityInsentient mob;
    private final IRangedEntity rangedAttackMob;
    @Nullable
    private EntityLiving target;
    private int attackTime;
    private final double speedModifier;
    private int seeTime;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private final float attackRadiusSqr;

    public PathfinderGoalArrowAttack(IRangedEntity irangedentity, double d0, int i, float f) {
        this(irangedentity, d0, i, i, f);
    }

    public PathfinderGoalArrowAttack(IRangedEntity irangedentity, double d0, int i, int j, float f) {
        this.attackTime = -1;
        if (!(irangedentity instanceof EntityLiving)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        } else {
            this.rangedAttackMob = irangedentity;
            this.mob = (EntityInsentient) irangedentity;
            this.speedModifier = d0;
            this.attackIntervalMin = i;
            this.attackIntervalMax = j;
            this.attackRadius = f;
            this.attackRadiusSqr = f * f;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }
    }

    @Override
    public boolean canUse() {
        EntityLiving entityliving = this.mob.getTarget();

        if (entityliving != null && entityliving.isAlive()) {
            this.target = entityliving;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.target);

        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (d0 <= (double) this.attackRadiusSqr && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo((Entity) this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!flag) {
                return;
            }

            float f = (float) Math.sqrt(d0) / this.attackRadius;
            float f1 = MathHelper.clamp(f, 0.1F, 1.0F);

            this.rangedAttackMob.performRangedAttack(this.target, f1);
            this.attackTime = MathHelper.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = MathHelper.floor(MathHelper.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
        }

    }
}

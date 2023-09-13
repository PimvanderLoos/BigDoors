package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalLookAtPlayer extends PathfinderGoal {

    public static final float DEFAULT_PROBABILITY = 0.02F;
    protected final EntityInsentient mob;
    @Nullable
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    protected final float probability;
    private final boolean onlyHorizontal;
    protected final Class<? extends EntityLiving> lookAtType;
    protected final PathfinderTargetCondition lookAtContext;

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends EntityLiving> oclass, float f) {
        this(entityinsentient, oclass, f, 0.02F);
    }

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends EntityLiving> oclass, float f, float f1) {
        this(entityinsentient, oclass, f, f1, false);
    }

    public PathfinderGoalLookAtPlayer(EntityInsentient entityinsentient, Class<? extends EntityLiving> oclass, float f, float f1, boolean flag) {
        this.mob = entityinsentient;
        this.lookAtType = oclass;
        this.lookDistance = f;
        this.probability = f1;
        this.onlyHorizontal = flag;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.LOOK));
        if (oclass == EntityHuman.class) {
            this.lookAtContext = PathfinderTargetCondition.forNonCombat().range((double) f).selector((entityliving) -> {
                return IEntitySelector.notRiding(entityinsentient).test(entityliving);
            });
        } else {
            this.lookAtContext = PathfinderTargetCondition.forNonCombat().range((double) f);
        }

    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            if (this.mob.getTarget() != null) {
                this.lookAt = this.mob.getTarget();
            }

            if (this.lookAtType == EntityHuman.class) {
                this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            } else {
                this.lookAt = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double) this.lookDistance, 3.0D, (double) this.lookDistance), (entityliving) -> {
                    return true;
                }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }

            return this.lookAt != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.lookAt.isAlive() ? false : (this.mob.distanceToSqr(this.lookAt) > (double) (this.lookDistance * this.lookDistance) ? false : this.lookTime > 0);
    }

    @Override
    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.lookAt = null;
    }

    @Override
    public void tick() {
        if (this.lookAt.isAlive()) {
            double d0 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();

            this.mob.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
            --this.lookTime;
        }
    }
}

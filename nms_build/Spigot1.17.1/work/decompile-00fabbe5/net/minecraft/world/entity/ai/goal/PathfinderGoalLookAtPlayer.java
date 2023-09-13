package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalLookAtPlayer extends PathfinderGoal {

    public static final float DEFAULT_PROBABILITY = 0.02F;
    protected final EntityInsentient mob;
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
        this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        if (oclass == EntityHuman.class) {
            this.lookAtContext = PathfinderTargetCondition.b().a((double) f).a((entityliving) -> {
                return IEntitySelector.b(entityinsentient).test(entityliving);
            });
        } else {
            this.lookAtContext = PathfinderTargetCondition.b().a((double) f);
        }

    }

    @Override
    public boolean a() {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            if (this.mob.getGoalTarget() != null) {
                this.lookAt = this.mob.getGoalTarget();
            }

            if (this.lookAtType == EntityHuman.class) {
                this.lookAt = this.mob.level.a(this.lookAtContext, this.mob, this.mob.locX(), this.mob.getHeadY(), this.mob.locZ());
            } else {
                this.lookAt = this.mob.level.a(this.mob.level.a(this.lookAtType, this.mob.getBoundingBox().grow((double) this.lookDistance, 3.0D, (double) this.lookDistance), (entityliving) -> {
                    return true;
                }), this.lookAtContext, (EntityLiving) this.mob, this.mob.locX(), this.mob.getHeadY(), this.mob.locZ());
            }

            return this.lookAt != null;
        }
    }

    @Override
    public boolean b() {
        return !this.lookAt.isAlive() ? false : (this.mob.f(this.lookAt) > (double) (this.lookDistance * this.lookDistance) ? false : this.lookTime > 0);
    }

    @Override
    public void c() {
        this.lookTime = 40 + this.mob.getRandom().nextInt(40);
    }

    @Override
    public void d() {
        this.lookAt = null;
    }

    @Override
    public void e() {
        double d0 = this.onlyHorizontal ? this.mob.getHeadY() : this.lookAt.getHeadY();

        this.mob.getControllerLook().a(this.lookAt.locX(), d0, this.lookAt.locZ());
        --this.lookTime;
    }
}

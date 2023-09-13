package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalLeapAtTarget extends PathfinderGoal {

    private final EntityInsentient mob;
    private EntityLiving target;
    private final float yd;

    public PathfinderGoalLeapAtTarget(EntityInsentient entityinsentient, float f) {
        this.mob = entityinsentient;
        this.yd = f;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isVehicle()) {
            return false;
        } else {
            this.target = this.mob.getTarget();
            if (this.target == null) {
                return false;
            } else {
                double d0 = this.mob.distanceToSqr((Entity) this.target);

                return d0 >= 4.0D && d0 <= 16.0D ? (!this.mob.isOnGround() ? false : this.mob.getRandom().nextInt(reducedTickDelay(5)) == 0) : false;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.isOnGround();
    }

    @Override
    public void start() {
        Vec3D vec3d = this.mob.getDeltaMovement();
        Vec3D vec3d1 = new Vec3D(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());

        if (vec3d1.lengthSqr() > 1.0E-7D) {
            vec3d1 = vec3d1.normalize().scale(0.4D).add(vec3d.scale(0.2D));
        }

        this.mob.setDeltaMovement(vec3d1.x, (double) this.yd, vec3d1.z);
    }
}

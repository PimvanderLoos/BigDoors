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
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (this.mob.isVehicle()) {
            return false;
        } else {
            this.target = this.mob.getGoalTarget();
            if (this.target == null) {
                return false;
            } else {
                double d0 = this.mob.f((Entity) this.target);

                return d0 >= 4.0D && d0 <= 16.0D ? (!this.mob.isOnGround() ? false : this.mob.getRandom().nextInt(5) == 0) : false;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.mob.isOnGround();
    }

    @Override
    public void c() {
        Vec3D vec3d = this.mob.getMot();
        Vec3D vec3d1 = new Vec3D(this.target.locX() - this.mob.locX(), 0.0D, this.target.locZ() - this.mob.locZ());

        if (vec3d1.g() > 1.0E-7D) {
            vec3d1 = vec3d1.d().a(0.4D).e(vec3d.a(0.2D));
        }

        this.mob.setMot(vec3d1.x, (double) this.yd, vec3d1.z);
    }
}

package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalMoveTowardsTarget extends PathfinderGoal {

    private final EntityCreature a;
    private EntityLiving b;
    private double c;
    private double d;
    private double e;
    private final double f;
    private final float g;

    public PathfinderGoalMoveTowardsTarget(EntityCreature entitycreature, double d0, float f) {
        this.a = entitycreature;
        this.f = d0;
        this.g = f;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        this.b = this.a.getGoalTarget();
        if (this.b == null) {
            return false;
        } else if (this.b.h((Entity) this.a) > (double) (this.g * this.g)) {
            return false;
        } else {
            Vec3D vec3d = RandomPositionGenerator.b(this.a, 16, 7, this.b.getPositionVector());

            if (vec3d == null) {
                return false;
            } else {
                this.c = vec3d.x;
                this.d = vec3d.y;
                this.e = vec3d.z;
                return true;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.a.getNavigation().m() && this.b.isAlive() && this.b.h((Entity) this.a) < (double) (this.g * this.g);
    }

    @Override
    public void d() {
        this.b = null;
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.c, this.d, this.e, this.f);
    }
}

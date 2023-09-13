package net.minecraft.server;

import java.util.EnumSet;

public class PathfinderGoalMoveTowardsRestriction extends PathfinderGoal {

    private final EntityCreature a;
    private double b;
    private double c;
    private double d;
    private final double e;

    public PathfinderGoalMoveTowardsRestriction(EntityCreature entitycreature, double d0) {
        this.a = entitycreature;
        this.e = d0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (this.a.ew()) {
            return false;
        } else {
            Vec3D vec3d = RandomPositionGenerator.b(this.a, 16, 7, Vec3D.c((BaseBlockPosition) this.a.ex()));

            if (vec3d == null) {
                return false;
            } else {
                this.b = vec3d.x;
                this.c = vec3d.y;
                this.d = vec3d.z;
                return true;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.a.getNavigation().m();
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.b, this.c, this.d, this.e);
    }
}

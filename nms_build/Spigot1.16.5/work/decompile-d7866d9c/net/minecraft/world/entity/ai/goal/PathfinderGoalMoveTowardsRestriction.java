package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.phys.Vec3D;

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
        if (this.a.ev()) {
            return false;
        } else {
            Vec3D vec3d = RandomPositionGenerator.b(this.a, 16, 7, Vec3D.c((BaseBlockPosition) this.a.ew()));

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

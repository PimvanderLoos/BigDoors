package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalMoveTowardsRestriction extends PathfinderGoal {

    private final EntityCreature mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;

    public PathfinderGoalMoveTowardsRestriction(EntityCreature entitycreature, double d0) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (this.mob.fh()) {
            return false;
        } else {
            Vec3D vec3d = DefaultRandomPos.a(this.mob, 16, 7, Vec3D.c((BaseBlockPosition) this.mob.fi()), 1.5707963705062866D);

            if (vec3d == null) {
                return false;
            } else {
                this.wantedX = vec3d.x;
                this.wantedY = vec3d.y;
                this.wantedZ = vec3d.z;
                return true;
            }
        }
    }

    @Override
    public boolean b() {
        return !this.mob.getNavigation().m();
    }

    @Override
    public void c() {
        this.mob.getNavigation().a(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
}

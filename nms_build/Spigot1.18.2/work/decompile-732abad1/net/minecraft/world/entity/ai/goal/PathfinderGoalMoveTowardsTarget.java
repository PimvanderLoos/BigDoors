package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalMoveTowardsTarget extends PathfinderGoal {

    private final EntityCreature mob;
    @Nullable
    private EntityLiving target;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final float within;

    public PathfinderGoalMoveTowardsTarget(EntityCreature entitycreature, double d0, float f) {
        this.mob = entitycreature;
        this.speedModifier = d0;
        this.within = f;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        } else if (this.target.distanceToSqr((Entity) this.mob) > (double) (this.within * this.within)) {
            return false;
        } else {
            Vec3D vec3d = DefaultRandomPos.getPosTowards(this.mob, 16, 7, this.target.position(), 1.5707963705062866D);

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
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && this.target.isAlive() && this.target.distanceToSqr((Entity) this.mob) < (double) (this.within * this.within);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }
}

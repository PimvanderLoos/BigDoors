package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.Vec3D;

public class DragonControllerLandingFly extends AbstractDragonController {

    private static final PathfinderTargetCondition NEAR_EGG_TARGETING = PathfinderTargetCondition.forCombat().ignoreLineOfSight();
    @Nullable
    private PathEntity currentPath;
    @Nullable
    private Vec3D targetLocation;

    public DragonControllerLandingFly(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public DragonControllerPhase<DragonControllerLandingFly> getPhase() {
        return DragonControllerPhase.LANDING_APPROACH;
    }

    @Override
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Override
    public void doServerTick() {
        double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        if (d0 < 100.0D || d0 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.findNewTarget();
        }

    }

    @Nullable
    @Override
    public Vec3D getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int i = this.dragon.findClosestNode();
            BlockPosition blockposition = this.dragon.level.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION);
            EntityHuman entityhuman = this.dragon.level.getNearestPlayer(DragonControllerLandingFly.NEAR_EGG_TARGETING, this.dragon, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
            int j;

            if (entityhuman != null) {
                Vec3D vec3d = (new Vec3D(entityhuman.getX(), 0.0D, entityhuman.getZ())).normalize();

                j = this.dragon.findClosestNode(-vec3d.x * 40.0D, 105.0D, -vec3d.z * 40.0D);
            } else {
                j = this.dragon.findClosestNode(40.0D, (double) blockposition.getY(), 0.0D);
            }

            PathPoint pathpoint = new PathPoint(blockposition.getX(), blockposition.getY(), blockposition.getZ());

            this.currentPath = this.dragon.findPath(i, j, pathpoint);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }

        this.navigateToNextPathNode();
        if (this.currentPath != null && this.currentPath.isDone()) {
            this.dragon.getPhaseManager().setPhase(DragonControllerPhase.LANDING);
        }

    }

    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isDone()) {
            BlockPosition blockposition = this.currentPath.getNextNodePos();

            this.currentPath.advance();
            double d0 = (double) blockposition.getX();
            double d1 = (double) blockposition.getZ();

            double d2;

            do {
                d2 = (double) ((float) blockposition.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
            } while (d2 < (double) blockposition.getY());

            this.targetLocation = new Vec3D(d0, d2, d1);
        }

    }
}

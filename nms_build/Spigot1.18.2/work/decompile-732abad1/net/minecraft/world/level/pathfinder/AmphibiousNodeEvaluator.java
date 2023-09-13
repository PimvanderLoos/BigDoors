package net.minecraft.world.level.pathfinder;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;

public class AmphibiousNodeEvaluator extends PathfinderNormal {

    private final boolean prefersShallowSwimming;
    private float oldWalkableCost;
    private float oldWaterBorderCost;

    public AmphibiousNodeEvaluator(boolean flag) {
        this.prefersShallowSwimming = flag;
    }

    @Override
    public void prepare(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.prepare(chunkcache, entityinsentient);
        entityinsentient.setPathfindingMalus(PathType.WATER, 0.0F);
        this.oldWalkableCost = entityinsentient.getPathfindingMalus(PathType.WALKABLE);
        entityinsentient.setPathfindingMalus(PathType.WALKABLE, 6.0F);
        this.oldWaterBorderCost = entityinsentient.getPathfindingMalus(PathType.WATER_BORDER);
        entityinsentient.setPathfindingMalus(PathType.WATER_BORDER, 4.0F);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(PathType.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(PathType.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }

    @Override
    public PathPoint getStart() {
        return this.getNode(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY + 0.5D), MathHelper.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public PathDestination getGoal(double d0, double d1, double d2) {
        return new PathDestination(this.getNode(MathHelper.floor(d0), MathHelper.floor(d1 + 0.5D), MathHelper.floor(d2)));
    }

    @Override
    public int getNeighbors(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = super.getNeighbors(apathpoint, pathpoint);
        PathType pathtype = this.getCachedBlockType(this.mob, pathpoint.x, pathpoint.y + 1, pathpoint.z);
        PathType pathtype1 = this.getCachedBlockType(this.mob, pathpoint.x, pathpoint.y, pathpoint.z);
        int j;

        if (this.mob.getPathfindingMalus(pathtype) >= 0.0F && pathtype1 != PathType.STICKY_HONEY) {
            j = MathHelper.floor(Math.max(1.0F, this.mob.maxUpStep));
        } else {
            j = 0;
        }

        double d0 = this.getFloorLevel(new BlockPosition(pathpoint.x, pathpoint.y, pathpoint.z));
        PathPoint pathpoint1 = this.findAcceptedNode(pathpoint.x, pathpoint.y + 1, pathpoint.z, Math.max(0, j - 1), d0, EnumDirection.UP, pathtype1);
        PathPoint pathpoint2 = this.findAcceptedNode(pathpoint.x, pathpoint.y - 1, pathpoint.z, j, d0, EnumDirection.DOWN, pathtype1);

        if (this.isNeighborValid(pathpoint1, pathpoint)) {
            apathpoint[i++] = pathpoint1;
        }

        if (this.isNeighborValid(pathpoint2, pathpoint) && pathtype1 != PathType.TRAPDOOR) {
            apathpoint[i++] = pathpoint2;
        }

        for (int k = 0; k < i; ++k) {
            PathPoint pathpoint3 = apathpoint[k];

            if (pathpoint3.type == PathType.WATER && this.prefersShallowSwimming && pathpoint3.y < this.mob.level.getSeaLevel() - 10) {
                ++pathpoint3.costMalus;
            }
        }

        return i;
    }

    @Override
    protected double getFloorLevel(BlockPosition blockposition) {
        return this.mob.isInWater() ? (double) blockposition.getY() + 0.5D : super.getFloorLevel(blockposition);
    }

    @Override
    protected boolean isAmphibious() {
        return true;
    }

    @Override
    public PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        PathType pathtype = getBlockPathTypeRaw(iblockaccess, blockposition_mutableblockposition.set(i, j, k));

        if (pathtype == PathType.WATER) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int l = aenumdirection.length;

            for (int i1 = 0; i1 < l; ++i1) {
                EnumDirection enumdirection = aenumdirection[i1];
                PathType pathtype1 = getBlockPathTypeRaw(iblockaccess, blockposition_mutableblockposition.set(i, j, k).move(enumdirection));

                if (pathtype1 == PathType.BLOCKED) {
                    return PathType.WATER_BORDER;
                }
            }

            return PathType.WATER;
        } else {
            return getBlockPathTypeStatic(iblockaccess, blockposition_mutableblockposition);
        }
    }
}

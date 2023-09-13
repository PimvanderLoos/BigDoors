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
    public void a(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.a(chunkcache, entityinsentient);
        entityinsentient.a(PathType.WATER, 0.0F);
        this.oldWalkableCost = entityinsentient.a(PathType.WALKABLE);
        entityinsentient.a(PathType.WALKABLE, 6.0F);
        this.oldWaterBorderCost = entityinsentient.a(PathType.WATER_BORDER);
        entityinsentient.a(PathType.WATER_BORDER, 4.0F);
    }

    @Override
    public void a() {
        this.mob.a(PathType.WALKABLE, this.oldWalkableCost);
        this.mob.a(PathType.WATER_BORDER, this.oldWaterBorderCost);
        super.a();
    }

    @Override
    public PathPoint b() {
        return this.a(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY + 0.5D), MathHelper.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public PathDestination a(double d0, double d1, double d2) {
        return new PathDestination(this.a(MathHelper.floor(d0), MathHelper.floor(d1 + 0.5D), MathHelper.floor(d2)));
    }

    @Override
    public int a(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = super.a(apathpoint, pathpoint);
        PathType pathtype = this.a(this.mob, pathpoint.x, pathpoint.y + 1, pathpoint.z);
        PathType pathtype1 = this.a(this.mob, pathpoint.x, pathpoint.y, pathpoint.z);
        int j;

        if (this.mob.a(pathtype) >= 0.0F && pathtype1 != PathType.STICKY_HONEY) {
            j = MathHelper.d(Math.max(1.0F, this.mob.maxUpStep));
        } else {
            j = 0;
        }

        double d0 = this.a(new BlockPosition(pathpoint.x, pathpoint.y, pathpoint.z));
        PathPoint pathpoint1 = this.a(pathpoint.x, pathpoint.y + 1, pathpoint.z, Math.max(0, j - 1), d0, EnumDirection.UP, pathtype1);
        PathPoint pathpoint2 = this.a(pathpoint.x, pathpoint.y - 1, pathpoint.z, j, d0, EnumDirection.DOWN, pathtype1);

        if (this.a(pathpoint1, pathpoint)) {
            apathpoint[i++] = pathpoint1;
        }

        if (this.a(pathpoint2, pathpoint) && pathtype1 != PathType.TRAPDOOR) {
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
    protected double a(BlockPosition blockposition) {
        return this.mob.isInWater() ? (double) blockposition.getY() + 0.5D : super.a(blockposition);
    }

    @Override
    protected boolean c() {
        return true;
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        PathType pathtype = b(iblockaccess, blockposition_mutableblockposition.d(i, j, k));

        if (pathtype == PathType.WATER) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int l = aenumdirection.length;

            for (int i1 = 0; i1 < l; ++i1) {
                EnumDirection enumdirection = aenumdirection[i1];
                PathType pathtype1 = b(iblockaccess, blockposition_mutableblockposition.d(i, j, k).c(enumdirection));

                if (pathtype1 == PathType.BLOCKED) {
                    return PathType.WATER_BORDER;
                }
            }

            return PathType.WATER;
        } else {
            return a(iblockaccess, blockposition_mutableblockposition);
        }
    }
}

package net.minecraft.server;

import javax.annotation.Nullable;

public class PathfinderWater extends PathfinderAbstract {

    public PathfinderWater() {}

    public PathPoint b() {
        return this.a(MathHelper.floor(this.b.getBoundingBox().a), MathHelper.floor(this.b.getBoundingBox().b + 0.5D), MathHelper.floor(this.b.getBoundingBox().c));
    }

    public PathPoint a(double d0, double d1, double d2) {
        return this.a(MathHelper.floor(d0 - (double) (this.b.width / 2.0F)), MathHelper.floor(d1 + 0.5D), MathHelper.floor(d2 - (double) (this.b.width / 2.0F)));
    }

    public int a(PathPoint[] apathpoint, PathPoint pathpoint, PathPoint pathpoint1, float f) {
        int i = 0;
        EnumDirection[] aenumdirection = EnumDirection.values();
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];
            PathPoint pathpoint2 = this.b(pathpoint.a + enumdirection.getAdjacentX(), pathpoint.b + enumdirection.getAdjacentY(), pathpoint.c + enumdirection.getAdjacentZ());

            if (pathpoint2 != null && !pathpoint2.i && pathpoint2.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint2;
            }
        }

        return i;
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        return PathType.WATER;
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        return PathType.WATER;
    }

    @Nullable
    private PathPoint b(int i, int j, int k) {
        PathType pathtype = this.c(i, j, k);

        return pathtype == PathType.WATER ? this.a(i, j, k) : null;
    }

    private PathType c(int i, int j, int k) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = i; l < i + this.d; ++l) {
            for (int i1 = j; i1 < j + this.e; ++i1) {
                for (int j1 = k; j1 < k + this.f; ++j1) {
                    IBlockData iblockdata = this.a.getType(blockposition_mutableblockposition.c(l, i1, j1));

                    if (iblockdata.getMaterial() != Material.WATER) {
                        return PathType.BLOCKED;
                    }
                }
            }
        }

        return PathType.WATER;
    }
}

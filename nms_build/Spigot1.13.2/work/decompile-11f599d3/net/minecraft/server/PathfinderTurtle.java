package net.minecraft.server;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PathfinderTurtle extends PathfinderNormal {

    private float k;
    private float l;

    public PathfinderTurtle() {}

    public void a(IBlockAccess iblockaccess, EntityInsentient entityinsentient) {
        super.a(iblockaccess, entityinsentient);
        entityinsentient.a(PathType.WATER, 0.0F);
        this.k = entityinsentient.a(PathType.WALKABLE);
        entityinsentient.a(PathType.WALKABLE, 6.0F);
        this.l = entityinsentient.a(PathType.WATER_BORDER);
        entityinsentient.a(PathType.WATER_BORDER, 4.0F);
    }

    public void a() {
        this.b.a(PathType.WALKABLE, this.k);
        this.b.a(PathType.WATER_BORDER, this.l);
        super.a();
    }

    public PathPoint b() {
        return this.a(MathHelper.floor(this.b.getBoundingBox().minX), MathHelper.floor(this.b.getBoundingBox().minY + 0.5D), MathHelper.floor(this.b.getBoundingBox().minZ));
    }

    public PathPoint a(double d0, double d1, double d2) {
        return this.a(MathHelper.floor(d0), MathHelper.floor(d1 + 0.5D), MathHelper.floor(d2));
    }

    public int a(PathPoint[] apathpoint, PathPoint pathpoint, PathPoint pathpoint1, float f) {
        int i = 0;
        boolean flag = true;
        BlockPosition blockposition = new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c);
        double d0 = this.a(blockposition);
        PathPoint pathpoint2 = this.a(pathpoint.a, pathpoint.b, pathpoint.c + 1, 1, d0);
        PathPoint pathpoint3 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c, 1, d0);
        PathPoint pathpoint4 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c, 1, d0);
        PathPoint pathpoint5 = this.a(pathpoint.a, pathpoint.b, pathpoint.c - 1, 1, d0);
        PathPoint pathpoint6 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c, 0, d0);
        PathPoint pathpoint7 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c, 1, d0);

        if (pathpoint2 != null && !pathpoint2.i && pathpoint2.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.i && pathpoint3.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint3;
        }

        if (pathpoint4 != null && !pathpoint4.i && pathpoint4.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint4;
        }

        if (pathpoint5 != null && !pathpoint5.i && pathpoint5.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint5;
        }

        if (pathpoint6 != null && !pathpoint6.i && pathpoint6.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint6;
        }

        if (pathpoint7 != null && !pathpoint7.i && pathpoint7.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint7;
        }

        boolean flag1 = pathpoint5 == null || pathpoint5.m == PathType.OPEN || pathpoint5.l != 0.0F;
        boolean flag2 = pathpoint2 == null || pathpoint2.m == PathType.OPEN || pathpoint2.l != 0.0F;
        boolean flag3 = pathpoint4 == null || pathpoint4.m == PathType.OPEN || pathpoint4.l != 0.0F;
        boolean flag4 = pathpoint3 == null || pathpoint3.m == PathType.OPEN || pathpoint3.l != 0.0F;
        PathPoint pathpoint8;

        if (flag1 && flag4) {
            pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c - 1, 1, d0);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag1 && flag3) {
            pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c - 1, 1, d0);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag2 && flag4) {
            pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c + 1, 1, d0);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag2 && flag3) {
            pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c + 1, 1, d0);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        return i;
    }

    private double a(BlockPosition blockposition) {
        if (!this.b.isInWater()) {
            BlockPosition blockposition1 = blockposition.down();
            VoxelShape voxelshape = this.a.getType(blockposition1).getCollisionShape(this.a, blockposition1);

            return (double) blockposition1.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.c(EnumDirection.EnumAxis.Y));
        } else {
            return (double) blockposition.getY() + 0.5D;
        }
    }

    @Nullable
    private PathPoint a(int i, int j, int k, int l, double d0) {
        PathPoint pathpoint = null;
        BlockPosition blockposition = new BlockPosition(i, j, k);
        double d1 = this.a(blockposition);

        if (d1 - d0 > 1.125D) {
            return null;
        } else {
            PathType pathtype = this.a(this.a, i, j, k, this.b, this.d, this.e, this.f, false, false);
            float f = this.b.a(pathtype);
            double d2 = (double) this.b.width / 2.0D;

            if (f >= 0.0F) {
                pathpoint = this.a(i, j, k);
                pathpoint.m = pathtype;
                pathpoint.l = Math.max(pathpoint.l, f);
            }

            if (pathtype != PathType.WATER && pathtype != PathType.WALKABLE) {
                if (pathpoint == null && l > 0 && pathtype != PathType.FENCE && pathtype != PathType.TRAPDOOR) {
                    pathpoint = this.a(i, j + 1, k, l - 1, d0);
                }

                if (pathtype == PathType.OPEN) {
                    AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) i - d2 + 0.5D, (double) j + 0.001D, (double) k - d2 + 0.5D, (double) i + d2 + 0.5D, (double) ((float) j + this.b.length), (double) k + d2 + 0.5D);

                    if (!this.b.world.getCubes((Entity) null, axisalignedbb)) {
                        return null;
                    }

                    PathType pathtype1 = this.a(this.a, i, j - 1, k, this.b, this.d, this.e, this.f, false, false);

                    if (pathtype1 == PathType.BLOCKED) {
                        pathpoint = this.a(i, j, k);
                        pathpoint.m = PathType.WALKABLE;
                        pathpoint.l = Math.max(pathpoint.l, f);
                        return pathpoint;
                    }

                    if (pathtype1 == PathType.WATER) {
                        pathpoint = this.a(i, j, k);
                        pathpoint.m = PathType.WATER;
                        pathpoint.l = Math.max(pathpoint.l, f);
                        return pathpoint;
                    }

                    int i1 = 0;

                    while (j > 0 && pathtype == PathType.OPEN) {
                        --j;
                        if (i1++ >= this.b.bn()) {
                            return null;
                        }

                        pathtype = this.a(this.a, i, j, k, this.b, this.d, this.e, this.f, false, false);
                        f = this.b.a(pathtype);
                        if (pathtype != PathType.OPEN && f >= 0.0F) {
                            pathpoint = this.a(i, j, k);
                            pathpoint.m = pathtype;
                            pathpoint.l = Math.max(pathpoint.l, f);
                            break;
                        }

                        if (f < 0.0F) {
                            return null;
                        }
                    }
                }

                return pathpoint;
            } else {
                if (j < this.b.world.getSeaLevel() - 10 && pathpoint != null) {
                    ++pathpoint.l;
                }

                return pathpoint;
            }
        }
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, int l, int i1, int j1, boolean flag, boolean flag1, EnumSet<PathType> enumset, PathType pathtype, BlockPosition blockposition) {
        for (int k1 = 0; k1 < l; ++k1) {
            for (int l1 = 0; l1 < i1; ++l1) {
                for (int i2 = 0; i2 < j1; ++i2) {
                    int j2 = k1 + i;
                    int k2 = l1 + j;
                    int l2 = i2 + k;
                    PathType pathtype1 = this.a(iblockaccess, j2, k2, l2);

                    if (pathtype1 == PathType.RAIL && !(iblockaccess.getType(blockposition).getBlock() instanceof BlockMinecartTrackAbstract) && !(iblockaccess.getType(blockposition.down()).getBlock() instanceof BlockMinecartTrackAbstract)) {
                        pathtype1 = PathType.FENCE;
                    }

                    if (pathtype1 == PathType.DOOR_OPEN || pathtype1 == PathType.DOOR_WOOD_CLOSED || pathtype1 == PathType.DOOR_IRON_CLOSED) {
                        pathtype1 = PathType.BLOCKED;
                    }

                    if (k1 == 0 && l1 == 0 && i2 == 0) {
                        pathtype = pathtype1;
                    }

                    enumset.add(pathtype1);
                }
            }
        }

        return pathtype;
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        PathType pathtype = this.b(iblockaccess, i, j, k);

        if (pathtype == PathType.WATER) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int l = aenumdirection.length;

            for (int i1 = 0; i1 < l; ++i1) {
                EnumDirection enumdirection = aenumdirection[i1];
                PathType pathtype1 = this.b(iblockaccess, i + enumdirection.getAdjacentX(), j + enumdirection.getAdjacentY(), k + enumdirection.getAdjacentZ());

                if (pathtype1 == PathType.BLOCKED) {
                    return PathType.WATER_BORDER;
                }
            }

            return PathType.WATER;
        } else {
            if (pathtype == PathType.OPEN && j >= 1) {
                Block block = iblockaccess.getType(new BlockPosition(i, j - 1, k)).getBlock();
                PathType pathtype2 = this.b(iblockaccess, i, j - 1, k);

                if (pathtype2 != PathType.WALKABLE && pathtype2 != PathType.OPEN && pathtype2 != PathType.LAVA) {
                    pathtype = PathType.WALKABLE;
                } else {
                    pathtype = PathType.OPEN;
                }

                if (pathtype2 == PathType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK) {
                    pathtype = PathType.DAMAGE_FIRE;
                }

                if (pathtype2 == PathType.DAMAGE_CACTUS) {
                    pathtype = PathType.DAMAGE_CACTUS;
                }
            }

            pathtype = this.a(iblockaccess, i, j, k, pathtype);
            return pathtype;
        }
    }
}

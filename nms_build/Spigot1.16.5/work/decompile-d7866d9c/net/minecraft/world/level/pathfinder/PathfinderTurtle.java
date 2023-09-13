package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathfinderTurtle extends PathfinderNormal {

    private float k;
    private float l;

    public PathfinderTurtle() {}

    @Override
    public void a(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.a(chunkcache, entityinsentient);
        entityinsentient.a(PathType.WATER, 0.0F);
        this.k = entityinsentient.a(PathType.WALKABLE);
        entityinsentient.a(PathType.WALKABLE, 6.0F);
        this.l = entityinsentient.a(PathType.WATER_BORDER);
        entityinsentient.a(PathType.WATER_BORDER, 4.0F);
    }

    @Override
    public void a() {
        this.b.a(PathType.WALKABLE, this.k);
        this.b.a(PathType.WATER_BORDER, this.l);
        super.a();
    }

    @Override
    public PathPoint b() {
        return this.a(MathHelper.floor(this.b.getBoundingBox().minX), MathHelper.floor(this.b.getBoundingBox().minY + 0.5D), MathHelper.floor(this.b.getBoundingBox().minZ));
    }

    @Override
    public PathDestination a(double d0, double d1, double d2) {
        return new PathDestination(this.a(MathHelper.floor(d0), MathHelper.floor(d1 + 0.5D), MathHelper.floor(d2)));
    }

    @Override
    public int a(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = 0;
        boolean flag = true;
        BlockPosition blockposition = new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c);
        double d0 = this.b(blockposition);
        PathPoint pathpoint1 = this.a(pathpoint.a, pathpoint.b, pathpoint.c + 1, 1, d0);
        PathPoint pathpoint2 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c, 1, d0);
        PathPoint pathpoint3 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c, 1, d0);
        PathPoint pathpoint4 = this.a(pathpoint.a, pathpoint.b, pathpoint.c - 1, 1, d0);
        PathPoint pathpoint5 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c, 0, d0);
        PathPoint pathpoint6 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c, 1, d0);

        if (pathpoint1 != null && !pathpoint1.i) {
            apathpoint[i++] = pathpoint1;
        }

        if (pathpoint2 != null && !pathpoint2.i) {
            apathpoint[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.i) {
            apathpoint[i++] = pathpoint3;
        }

        if (pathpoint4 != null && !pathpoint4.i) {
            apathpoint[i++] = pathpoint4;
        }

        if (pathpoint5 != null && !pathpoint5.i) {
            apathpoint[i++] = pathpoint5;
        }

        if (pathpoint6 != null && !pathpoint6.i) {
            apathpoint[i++] = pathpoint6;
        }

        boolean flag1 = pathpoint4 == null || pathpoint4.l == PathType.OPEN || pathpoint4.k != 0.0F;
        boolean flag2 = pathpoint1 == null || pathpoint1.l == PathType.OPEN || pathpoint1.k != 0.0F;
        boolean flag3 = pathpoint3 == null || pathpoint3.l == PathType.OPEN || pathpoint3.k != 0.0F;
        boolean flag4 = pathpoint2 == null || pathpoint2.l == PathType.OPEN || pathpoint2.k != 0.0F;
        PathPoint pathpoint7;

        if (flag1 && flag4) {
            pathpoint7 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c - 1, 1, d0);
            if (pathpoint7 != null && !pathpoint7.i) {
                apathpoint[i++] = pathpoint7;
            }
        }

        if (flag1 && flag3) {
            pathpoint7 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c - 1, 1, d0);
            if (pathpoint7 != null && !pathpoint7.i) {
                apathpoint[i++] = pathpoint7;
            }
        }

        if (flag2 && flag4) {
            pathpoint7 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c + 1, 1, d0);
            if (pathpoint7 != null && !pathpoint7.i) {
                apathpoint[i++] = pathpoint7;
            }
        }

        if (flag2 && flag3) {
            pathpoint7 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c + 1, 1, d0);
            if (pathpoint7 != null && !pathpoint7.i) {
                apathpoint[i++] = pathpoint7;
            }
        }

        return i;
    }

    private double b(BlockPosition blockposition) {
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
        double d1 = this.b(blockposition);

        if (d1 - d0 > 1.125D) {
            return null;
        } else {
            PathType pathtype = this.a(this.a, i, j, k, this.b, this.d, this.e, this.f, false, false);
            float f = this.b.a(pathtype);
            double d2 = (double) this.b.getWidth() / 2.0D;

            if (f >= 0.0F) {
                pathpoint = this.a(i, j, k);
                pathpoint.l = pathtype;
                pathpoint.k = Math.max(pathpoint.k, f);
            }

            if (pathtype != PathType.WATER && pathtype != PathType.WALKABLE) {
                if (pathpoint == null && l > 0 && pathtype != PathType.FENCE && pathtype != PathType.UNPASSABLE_RAIL && pathtype != PathType.TRAPDOOR) {
                    pathpoint = this.a(i, j + 1, k, l - 1, d0);
                }

                if (pathtype == PathType.OPEN) {
                    AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) i - d2 + 0.5D, (double) j + 0.001D, (double) k - d2 + 0.5D, (double) i + d2 + 0.5D, (double) ((float) j + this.b.getHeight()), (double) k + d2 + 0.5D);

                    if (!this.b.world.getCubes(this.b, axisalignedbb)) {
                        return null;
                    }

                    PathType pathtype1 = this.a(this.a, i, j - 1, k, this.b, this.d, this.e, this.f, false, false);

                    if (pathtype1 == PathType.BLOCKED) {
                        pathpoint = this.a(i, j, k);
                        pathpoint.l = PathType.WALKABLE;
                        pathpoint.k = Math.max(pathpoint.k, f);
                        return pathpoint;
                    }

                    if (pathtype1 == PathType.WATER) {
                        pathpoint = this.a(i, j, k);
                        pathpoint.l = PathType.WATER;
                        pathpoint.k = Math.max(pathpoint.k, f);
                        return pathpoint;
                    }

                    int i1 = 0;

                    while (j > 0 && pathtype == PathType.OPEN) {
                        --j;
                        if (i1++ >= this.b.bP()) {
                            return null;
                        }

                        pathtype = this.a(this.a, i, j, k, this.b, this.d, this.e, this.f, false, false);
                        f = this.b.a(pathtype);
                        if (pathtype != PathType.OPEN && f >= 0.0F) {
                            pathpoint = this.a(i, j, k);
                            pathpoint.l = pathtype;
                            pathpoint.k = Math.max(pathpoint.k, f);
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
                    ++pathpoint.k;
                }

                return pathpoint;
            }
        }
    }

    @Override
    protected PathType a(IBlockAccess iblockaccess, boolean flag, boolean flag1, BlockPosition blockposition, PathType pathtype) {
        if (pathtype == PathType.RAIL && !(iblockaccess.getType(blockposition).getBlock() instanceof BlockMinecartTrackAbstract) && !(iblockaccess.getType(blockposition.down()).getBlock() instanceof BlockMinecartTrackAbstract)) {
            pathtype = PathType.UNPASSABLE_RAIL;
        }

        if (pathtype == PathType.DOOR_OPEN || pathtype == PathType.DOOR_WOOD_CLOSED || pathtype == PathType.DOOR_IRON_CLOSED) {
            pathtype = PathType.BLOCKED;
        }

        if (pathtype == PathType.LEAVES) {
            pathtype = PathType.BLOCKED;
        }

        return pathtype;
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
            if (pathtype == PathType.OPEN && j >= 1) {
                IBlockData iblockdata = iblockaccess.getType(new BlockPosition(i, j - 1, k));
                PathType pathtype2 = b(iblockaccess, blockposition_mutableblockposition.d(i, j - 1, k));

                if (pathtype2 != PathType.WALKABLE && pathtype2 != PathType.OPEN && pathtype2 != PathType.LAVA) {
                    pathtype = PathType.WALKABLE;
                } else {
                    pathtype = PathType.OPEN;
                }

                if (pathtype2 == PathType.DAMAGE_FIRE || iblockdata.a(Blocks.MAGMA_BLOCK) || iblockdata.a((Tag) TagsBlock.CAMPFIRES)) {
                    pathtype = PathType.DAMAGE_FIRE;
                }

                if (pathtype2 == PathType.DAMAGE_CACTUS) {
                    pathtype = PathType.DAMAGE_CACTUS;
                }

                if (pathtype2 == PathType.DAMAGE_OTHER) {
                    pathtype = PathType.DAMAGE_OTHER;
                }
            }

            if (pathtype == PathType.WALKABLE) {
                pathtype = a(iblockaccess, blockposition_mutableblockposition.d(i, j, k), pathtype);
            }

            return pathtype;
        }
    }
}

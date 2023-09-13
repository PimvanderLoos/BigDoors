package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

public class PathfinderFlying extends PathfinderNormal {

    public PathfinderFlying() {}

    public void a(IBlockAccess iblockaccess, EntityInsentient entityinsentient) {
        super.a(iblockaccess, entityinsentient);
        this.j = entityinsentient.a(PathType.WATER);
    }

    public void a() {
        this.b.a(PathType.WATER, this.j);
        super.a();
    }

    public PathPoint b() {
        int i;

        if (this.e() && this.b.isInWater()) {
            i = (int) this.b.getBoundingBox().minY;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(MathHelper.floor(this.b.locX), i, MathHelper.floor(this.b.locZ));

            for (Block block = this.a.getType(blockposition_mutableblockposition).getBlock(); block == Blocks.WATER; block = this.a.getType(blockposition_mutableblockposition).getBlock()) {
                ++i;
                blockposition_mutableblockposition.c(MathHelper.floor(this.b.locX), i, MathHelper.floor(this.b.locZ));
            }
        } else {
            i = MathHelper.floor(this.b.getBoundingBox().minY + 0.5D);
        }

        BlockPosition blockposition = new BlockPosition(this.b);
        PathType pathtype = this.a(this.b, blockposition.getX(), i, blockposition.getZ());

        if (this.b.a(pathtype) < 0.0F) {
            Set<BlockPosition> set = Sets.newHashSet();

            set.add(new BlockPosition(this.b.getBoundingBox().minX, (double) i, this.b.getBoundingBox().minZ));
            set.add(new BlockPosition(this.b.getBoundingBox().minX, (double) i, this.b.getBoundingBox().maxZ));
            set.add(new BlockPosition(this.b.getBoundingBox().maxX, (double) i, this.b.getBoundingBox().minZ));
            set.add(new BlockPosition(this.b.getBoundingBox().maxX, (double) i, this.b.getBoundingBox().maxZ));
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                PathType pathtype1 = this.a(this.b, blockposition1);

                if (this.b.a(pathtype1) >= 0.0F) {
                    return super.a(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
                }
            }
        }

        return super.a(blockposition.getX(), i, blockposition.getZ());
    }

    public PathPoint a(double d0, double d1, double d2) {
        return super.a(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
    }

    public int a(PathPoint[] apathpoint, PathPoint pathpoint, PathPoint pathpoint1, float f) {
        int i = 0;
        PathPoint pathpoint2 = this.a(pathpoint.a, pathpoint.b, pathpoint.c + 1);
        PathPoint pathpoint3 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c);
        PathPoint pathpoint4 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c);
        PathPoint pathpoint5 = this.a(pathpoint.a, pathpoint.b, pathpoint.c - 1);
        PathPoint pathpoint6 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c);
        PathPoint pathpoint7 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c);

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

        boolean flag = pathpoint5 == null || pathpoint5.l != 0.0F;
        boolean flag1 = pathpoint2 == null || pathpoint2.l != 0.0F;
        boolean flag2 = pathpoint4 == null || pathpoint4.l != 0.0F;
        boolean flag3 = pathpoint3 == null || pathpoint3.l != 0.0F;
        boolean flag4 = pathpoint6 == null || pathpoint6.l != 0.0F;
        boolean flag5 = pathpoint7 == null || pathpoint7.l != 0.0F;
        PathPoint pathpoint8;

        if (flag && flag3) {
            pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c - 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag && flag2) {
            pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c - 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag1 && flag3) {
            pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c + 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag1 && flag2) {
            pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c + 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag && flag4) {
            pathpoint8 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c - 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag1 && flag4) {
            pathpoint8 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c + 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag2 && flag4) {
            pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b + 1, pathpoint.c);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag3 && flag4) {
            pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b + 1, pathpoint.c);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag && flag5) {
            pathpoint8 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c - 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag1 && flag5) {
            pathpoint8 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c + 1);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag2 && flag5) {
            pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b - 1, pathpoint.c);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        if (flag3 && flag5) {
            pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b - 1, pathpoint.c);
            if (pathpoint8 != null && !pathpoint8.i && pathpoint8.a(pathpoint1) < f) {
                apathpoint[i++] = pathpoint8;
            }
        }

        return i;
    }

    @Nullable
    protected PathPoint a(int i, int j, int k) {
        PathPoint pathpoint = null;
        PathType pathtype = this.a(this.b, i, j, k);
        float f = this.b.a(pathtype);

        if (f >= 0.0F) {
            pathpoint = super.a(i, j, k);
            pathpoint.m = pathtype;
            pathpoint.l = Math.max(pathpoint.l, f);
            if (pathtype == PathType.WALKABLE) {
                ++pathpoint.l;
            }
        }

        return pathtype != PathType.OPEN && pathtype != PathType.WALKABLE ? pathpoint : pathpoint;
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);
        PathType pathtype = PathType.BLOCKED;
        BlockPosition blockposition = new BlockPosition(entityinsentient);

        pathtype = this.a(iblockaccess, i, j, k, l, i1, j1, flag, flag1, enumset, pathtype, blockposition);
        if (enumset.contains(PathType.FENCE)) {
            return PathType.FENCE;
        } else {
            PathType pathtype1 = PathType.BLOCKED;
            Iterator iterator = enumset.iterator();

            while (iterator.hasNext()) {
                PathType pathtype2 = (PathType) iterator.next();

                if (entityinsentient.a(pathtype2) < 0.0F) {
                    return pathtype2;
                }

                if (entityinsentient.a(pathtype2) >= entityinsentient.a(pathtype1)) {
                    pathtype1 = pathtype2;
                }
            }

            if (pathtype == PathType.OPEN && entityinsentient.a(pathtype1) == 0.0F) {
                return PathType.OPEN;
            } else {
                return pathtype1;
            }
        }
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        PathType pathtype = this.b(iblockaccess, i, j, k);

        if (pathtype == PathType.OPEN && j >= 1) {
            Block block = iblockaccess.getType(new BlockPosition(i, j - 1, k)).getBlock();
            PathType pathtype1 = this.b(iblockaccess, i, j - 1, k);

            if (pathtype1 != PathType.DAMAGE_FIRE && block != Blocks.MAGMA_BLOCK && pathtype1 != PathType.LAVA) {
                if (pathtype1 == PathType.DAMAGE_CACTUS) {
                    pathtype = PathType.DAMAGE_CACTUS;
                } else {
                    pathtype = pathtype1 != PathType.WALKABLE && pathtype1 != PathType.OPEN && pathtype1 != PathType.WATER ? PathType.WALKABLE : PathType.OPEN;
                }
            } else {
                pathtype = PathType.DAMAGE_FIRE;
            }
        }

        pathtype = this.a(iblockaccess, i, j, k, pathtype);
        return pathtype;
    }

    private PathType a(EntityInsentient entityinsentient, BlockPosition blockposition) {
        return this.a(entityinsentient, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    private PathType a(EntityInsentient entityinsentient, int i, int j, int k) {
        return this.a(this.a, i, j, k, entityinsentient, this.d, this.e, this.f, this.d(), this.c());
    }
}

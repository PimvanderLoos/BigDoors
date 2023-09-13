package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class PathfinderFlying extends PathfinderNormal {

    public PathfinderFlying() {}

    @Override
    public void a(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.a(chunkcache, entityinsentient);
        this.j = entityinsentient.a(PathType.WATER);
    }

    @Override
    public void a() {
        this.b.a(PathType.WATER, this.j);
        super.a();
    }

    @Override
    public PathPoint b() {
        int i;

        if (this.e() && this.b.isInWater()) {
            i = MathHelper.floor(this.b.locY());
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.b.locX(), (double) i, this.b.locZ());

            for (Block block = this.a.getType(blockposition_mutableblockposition).getBlock(); block == Blocks.WATER; block = this.a.getType(blockposition_mutableblockposition).getBlock()) {
                ++i;
                blockposition_mutableblockposition.c(this.b.locX(), (double) i, this.b.locZ());
            }
        } else {
            i = MathHelper.floor(this.b.locY() + 0.5D);
        }

        BlockPosition blockposition = this.b.getChunkCoordinates();
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

    @Override
    public PathDestination a(double d0, double d1, double d2) {
        return new PathDestination(super.a(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
    }

    @Override
    public int a(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = 0;
        PathPoint pathpoint1 = this.a(pathpoint.a, pathpoint.b, pathpoint.c + 1);

        if (this.b(pathpoint1)) {
            apathpoint[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c);

        if (this.b(pathpoint2)) {
            apathpoint[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c);

        if (this.b(pathpoint3)) {
            apathpoint[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.a(pathpoint.a, pathpoint.b, pathpoint.c - 1);

        if (this.b(pathpoint4)) {
            apathpoint[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c);

        if (this.b(pathpoint5)) {
            apathpoint[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c);

        if (this.b(pathpoint6)) {
            apathpoint[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c + 1);

        if (this.b(pathpoint7) && this.a(pathpoint1) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = this.a(pathpoint.a - 1, pathpoint.b + 1, pathpoint.c);

        if (this.b(pathpoint8) && this.a(pathpoint2) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint8;
        }

        PathPoint pathpoint9 = this.a(pathpoint.a + 1, pathpoint.b + 1, pathpoint.c);

        if (this.b(pathpoint9) && this.a(pathpoint3) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint9;
        }

        PathPoint pathpoint10 = this.a(pathpoint.a, pathpoint.b + 1, pathpoint.c - 1);

        if (this.b(pathpoint10) && this.a(pathpoint4) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint10;
        }

        PathPoint pathpoint11 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c + 1);

        if (this.b(pathpoint11) && this.a(pathpoint1) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint11;
        }

        PathPoint pathpoint12 = this.a(pathpoint.a - 1, pathpoint.b - 1, pathpoint.c);

        if (this.b(pathpoint12) && this.a(pathpoint2) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint12;
        }

        PathPoint pathpoint13 = this.a(pathpoint.a + 1, pathpoint.b - 1, pathpoint.c);

        if (this.b(pathpoint13) && this.a(pathpoint3) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint13;
        }

        PathPoint pathpoint14 = this.a(pathpoint.a, pathpoint.b - 1, pathpoint.c - 1);

        if (this.b(pathpoint14) && this.a(pathpoint4) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint14;
        }

        PathPoint pathpoint15 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c - 1);

        if (this.b(pathpoint15) && this.a(pathpoint4) && this.a(pathpoint3)) {
            apathpoint[i++] = pathpoint15;
        }

        PathPoint pathpoint16 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c + 1);

        if (this.b(pathpoint16) && this.a(pathpoint1) && this.a(pathpoint3)) {
            apathpoint[i++] = pathpoint16;
        }

        PathPoint pathpoint17 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c - 1);

        if (this.b(pathpoint17) && this.a(pathpoint4) && this.a(pathpoint2)) {
            apathpoint[i++] = pathpoint17;
        }

        PathPoint pathpoint18 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c + 1);

        if (this.b(pathpoint18) && this.a(pathpoint1) && this.a(pathpoint2)) {
            apathpoint[i++] = pathpoint18;
        }

        PathPoint pathpoint19 = this.a(pathpoint.a + 1, pathpoint.b + 1, pathpoint.c - 1);

        if (this.b(pathpoint19) && this.a(pathpoint15) && this.a(pathpoint4) && this.a(pathpoint3) && this.a(pathpoint5) && this.a(pathpoint10) && this.a(pathpoint9)) {
            apathpoint[i++] = pathpoint19;
        }

        PathPoint pathpoint20 = this.a(pathpoint.a + 1, pathpoint.b + 1, pathpoint.c + 1);

        if (this.b(pathpoint20) && this.a(pathpoint16) && this.a(pathpoint1) && this.a(pathpoint3) && this.a(pathpoint5) && this.a(pathpoint7) && this.a(pathpoint9)) {
            apathpoint[i++] = pathpoint20;
        }

        PathPoint pathpoint21 = this.a(pathpoint.a - 1, pathpoint.b + 1, pathpoint.c - 1);

        if (this.b(pathpoint21) && this.a(pathpoint17) && this.a(pathpoint4) && this.a(pathpoint2) & this.a(pathpoint5) && this.a(pathpoint10) && this.a(pathpoint8)) {
            apathpoint[i++] = pathpoint21;
        }

        PathPoint pathpoint22 = this.a(pathpoint.a - 1, pathpoint.b + 1, pathpoint.c + 1);

        if (this.b(pathpoint22) && this.a(pathpoint18) && this.a(pathpoint1) && this.a(pathpoint2) & this.a(pathpoint5) && this.a(pathpoint7) && this.a(pathpoint8)) {
            apathpoint[i++] = pathpoint22;
        }

        PathPoint pathpoint23 = this.a(pathpoint.a + 1, pathpoint.b - 1, pathpoint.c - 1);

        if (this.b(pathpoint23) && this.a(pathpoint15) && this.a(pathpoint4) && this.a(pathpoint3) && this.a(pathpoint6) && this.a(pathpoint14) && this.a(pathpoint13)) {
            apathpoint[i++] = pathpoint23;
        }

        PathPoint pathpoint24 = this.a(pathpoint.a + 1, pathpoint.b - 1, pathpoint.c + 1);

        if (this.b(pathpoint24) && this.a(pathpoint16) && this.a(pathpoint1) && this.a(pathpoint3) && this.a(pathpoint6) && this.a(pathpoint11) && this.a(pathpoint13)) {
            apathpoint[i++] = pathpoint24;
        }

        PathPoint pathpoint25 = this.a(pathpoint.a - 1, pathpoint.b - 1, pathpoint.c - 1);

        if (this.b(pathpoint25) && this.a(pathpoint17) && this.a(pathpoint4) && this.a(pathpoint2) && this.a(pathpoint6) && this.a(pathpoint14) && this.a(pathpoint12)) {
            apathpoint[i++] = pathpoint25;
        }

        PathPoint pathpoint26 = this.a(pathpoint.a - 1, pathpoint.b - 1, pathpoint.c + 1);

        if (this.b(pathpoint26) && this.a(pathpoint18) && this.a(pathpoint1) && this.a(pathpoint2) && this.a(pathpoint6) && this.a(pathpoint11) && this.a(pathpoint12)) {
            apathpoint[i++] = pathpoint26;
        }

        return i;
    }

    private boolean a(@Nullable PathPoint pathpoint) {
        return pathpoint != null && pathpoint.k >= 0.0F;
    }

    private boolean b(@Nullable PathPoint pathpoint) {
        return pathpoint != null && !pathpoint.i;
    }

    @Nullable
    @Override
    protected PathPoint a(int i, int j, int k) {
        PathPoint pathpoint = null;
        PathType pathtype = this.a(this.b, i, j, k);
        float f = this.b.a(pathtype);

        if (f >= 0.0F) {
            pathpoint = super.a(i, j, k);
            pathpoint.l = pathtype;
            pathpoint.k = Math.max(pathpoint.k, f);
            if (pathtype == PathType.WALKABLE) {
                ++pathpoint.k;
            }
        }

        return pathtype != PathType.OPEN && pathtype != PathType.WALKABLE ? pathpoint : pathpoint;
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);
        PathType pathtype = PathType.BLOCKED;
        BlockPosition blockposition = entityinsentient.getChunkCoordinates();

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

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        PathType pathtype = b(iblockaccess, blockposition_mutableblockposition.d(i, j, k));

        if (pathtype == PathType.OPEN && j >= 1) {
            IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition.d(i, j - 1, k));
            PathType pathtype1 = b(iblockaccess, blockposition_mutableblockposition.d(i, j - 1, k));

            if (pathtype1 != PathType.DAMAGE_FIRE && !iblockdata.a(Blocks.MAGMA_BLOCK) && pathtype1 != PathType.LAVA && !iblockdata.a((Tag) TagsBlock.CAMPFIRES)) {
                if (pathtype1 == PathType.DAMAGE_CACTUS) {
                    pathtype = PathType.DAMAGE_CACTUS;
                } else if (pathtype1 == PathType.DAMAGE_OTHER) {
                    pathtype = PathType.DAMAGE_OTHER;
                } else if (pathtype1 == PathType.COCOA) {
                    pathtype = PathType.COCOA;
                } else if (pathtype1 == PathType.FENCE) {
                    pathtype = PathType.FENCE;
                } else {
                    pathtype = pathtype1 != PathType.WALKABLE && pathtype1 != PathType.OPEN && pathtype1 != PathType.WATER ? PathType.WALKABLE : PathType.OPEN;
                }
            } else {
                pathtype = PathType.DAMAGE_FIRE;
            }
        }

        if (pathtype == PathType.WALKABLE || pathtype == PathType.OPEN) {
            pathtype = a(iblockaccess, blockposition_mutableblockposition.d(i, j, k), pathtype);
        }

        return pathtype;
    }

    private PathType a(EntityInsentient entityinsentient, BlockPosition blockposition) {
        return this.a(entityinsentient, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    private PathType a(EntityInsentient entityinsentient, int i, int j, int k) {
        return this.a(this.a, i, j, k, entityinsentient, this.d, this.e, this.f, this.d(), this.c());
    }
}

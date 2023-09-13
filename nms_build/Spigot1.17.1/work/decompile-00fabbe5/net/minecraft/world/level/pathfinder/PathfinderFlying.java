package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class PathfinderFlying extends PathfinderNormal {

    private final Long2ObjectMap<PathType> pathTypeByPosCache = new Long2ObjectOpenHashMap();

    public PathfinderFlying() {}

    @Override
    public void a(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.a(chunkcache, entityinsentient);
        this.pathTypeByPosCache.clear();
        this.oldWaterCost = entityinsentient.a(PathType.WATER);
    }

    @Override
    public void a() {
        this.mob.a(PathType.WATER, this.oldWaterCost);
        this.pathTypeByPosCache.clear();
        super.a();
    }

    @Override
    public PathPoint b() {
        int i;

        if (this.f() && this.mob.isInWater()) {
            i = this.mob.cY();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.mob.locX(), (double) i, this.mob.locZ());

            for (IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition); iblockdata.a(Blocks.WATER); iblockdata = this.level.getType(blockposition_mutableblockposition)) {
                ++i;
                blockposition_mutableblockposition.c(this.mob.locX(), (double) i, this.mob.locZ());
            }
        } else {
            i = MathHelper.floor(this.mob.locY() + 0.5D);
        }

        BlockPosition blockposition = this.mob.getChunkCoordinates();
        PathType pathtype = this.b(blockposition.getX(), i, blockposition.getZ());

        if (this.mob.a(pathtype) < 0.0F) {
            Set<BlockPosition> set = ImmutableSet.of(new BlockPosition(this.mob.getBoundingBox().minX, (double) i, this.mob.getBoundingBox().minZ), new BlockPosition(this.mob.getBoundingBox().minX, (double) i, this.mob.getBoundingBox().maxZ), new BlockPosition(this.mob.getBoundingBox().maxX, (double) i, this.mob.getBoundingBox().minZ), new BlockPosition(this.mob.getBoundingBox().maxX, (double) i, this.mob.getBoundingBox().maxZ));
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                PathType pathtype1 = this.b(blockposition.getX(), i, blockposition.getZ());

                if (this.mob.a(pathtype1) >= 0.0F) {
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
        PathPoint pathpoint1 = this.a(pathpoint.x, pathpoint.y, pathpoint.z + 1);

        if (this.b(pathpoint1)) {
            apathpoint[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.a(pathpoint.x - 1, pathpoint.y, pathpoint.z);

        if (this.b(pathpoint2)) {
            apathpoint[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.a(pathpoint.x + 1, pathpoint.y, pathpoint.z);

        if (this.b(pathpoint3)) {
            apathpoint[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.a(pathpoint.x, pathpoint.y, pathpoint.z - 1);

        if (this.b(pathpoint4)) {
            apathpoint[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.a(pathpoint.x, pathpoint.y + 1, pathpoint.z);

        if (this.b(pathpoint5)) {
            apathpoint[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.a(pathpoint.x, pathpoint.y - 1, pathpoint.z);

        if (this.b(pathpoint6)) {
            apathpoint[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.a(pathpoint.x, pathpoint.y + 1, pathpoint.z + 1);

        if (this.b(pathpoint7) && this.a(pathpoint1) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = this.a(pathpoint.x - 1, pathpoint.y + 1, pathpoint.z);

        if (this.b(pathpoint8) && this.a(pathpoint2) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint8;
        }

        PathPoint pathpoint9 = this.a(pathpoint.x + 1, pathpoint.y + 1, pathpoint.z);

        if (this.b(pathpoint9) && this.a(pathpoint3) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint9;
        }

        PathPoint pathpoint10 = this.a(pathpoint.x, pathpoint.y + 1, pathpoint.z - 1);

        if (this.b(pathpoint10) && this.a(pathpoint4) && this.a(pathpoint5)) {
            apathpoint[i++] = pathpoint10;
        }

        PathPoint pathpoint11 = this.a(pathpoint.x, pathpoint.y - 1, pathpoint.z + 1);

        if (this.b(pathpoint11) && this.a(pathpoint1) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint11;
        }

        PathPoint pathpoint12 = this.a(pathpoint.x - 1, pathpoint.y - 1, pathpoint.z);

        if (this.b(pathpoint12) && this.a(pathpoint2) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint12;
        }

        PathPoint pathpoint13 = this.a(pathpoint.x + 1, pathpoint.y - 1, pathpoint.z);

        if (this.b(pathpoint13) && this.a(pathpoint3) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint13;
        }

        PathPoint pathpoint14 = this.a(pathpoint.x, pathpoint.y - 1, pathpoint.z - 1);

        if (this.b(pathpoint14) && this.a(pathpoint4) && this.a(pathpoint6)) {
            apathpoint[i++] = pathpoint14;
        }

        PathPoint pathpoint15 = this.a(pathpoint.x + 1, pathpoint.y, pathpoint.z - 1);

        if (this.b(pathpoint15) && this.a(pathpoint4) && this.a(pathpoint3)) {
            apathpoint[i++] = pathpoint15;
        }

        PathPoint pathpoint16 = this.a(pathpoint.x + 1, pathpoint.y, pathpoint.z + 1);

        if (this.b(pathpoint16) && this.a(pathpoint1) && this.a(pathpoint3)) {
            apathpoint[i++] = pathpoint16;
        }

        PathPoint pathpoint17 = this.a(pathpoint.x - 1, pathpoint.y, pathpoint.z - 1);

        if (this.b(pathpoint17) && this.a(pathpoint4) && this.a(pathpoint2)) {
            apathpoint[i++] = pathpoint17;
        }

        PathPoint pathpoint18 = this.a(pathpoint.x - 1, pathpoint.y, pathpoint.z + 1);

        if (this.b(pathpoint18) && this.a(pathpoint1) && this.a(pathpoint2)) {
            apathpoint[i++] = pathpoint18;
        }

        PathPoint pathpoint19 = this.a(pathpoint.x + 1, pathpoint.y + 1, pathpoint.z - 1);

        if (this.b(pathpoint19) && this.a(pathpoint15) && this.a(pathpoint4) && this.a(pathpoint3) && this.a(pathpoint5) && this.a(pathpoint10) && this.a(pathpoint9)) {
            apathpoint[i++] = pathpoint19;
        }

        PathPoint pathpoint20 = this.a(pathpoint.x + 1, pathpoint.y + 1, pathpoint.z + 1);

        if (this.b(pathpoint20) && this.a(pathpoint16) && this.a(pathpoint1) && this.a(pathpoint3) && this.a(pathpoint5) && this.a(pathpoint7) && this.a(pathpoint9)) {
            apathpoint[i++] = pathpoint20;
        }

        PathPoint pathpoint21 = this.a(pathpoint.x - 1, pathpoint.y + 1, pathpoint.z - 1);

        if (this.b(pathpoint21) && this.a(pathpoint17) && this.a(pathpoint4) && this.a(pathpoint2) && this.a(pathpoint5) && this.a(pathpoint10) && this.a(pathpoint8)) {
            apathpoint[i++] = pathpoint21;
        }

        PathPoint pathpoint22 = this.a(pathpoint.x - 1, pathpoint.y + 1, pathpoint.z + 1);

        if (this.b(pathpoint22) && this.a(pathpoint18) && this.a(pathpoint1) && this.a(pathpoint2) && this.a(pathpoint5) && this.a(pathpoint7) && this.a(pathpoint8)) {
            apathpoint[i++] = pathpoint22;
        }

        PathPoint pathpoint23 = this.a(pathpoint.x + 1, pathpoint.y - 1, pathpoint.z - 1);

        if (this.b(pathpoint23) && this.a(pathpoint15) && this.a(pathpoint4) && this.a(pathpoint3) && this.a(pathpoint6) && this.a(pathpoint14) && this.a(pathpoint13)) {
            apathpoint[i++] = pathpoint23;
        }

        PathPoint pathpoint24 = this.a(pathpoint.x + 1, pathpoint.y - 1, pathpoint.z + 1);

        if (this.b(pathpoint24) && this.a(pathpoint16) && this.a(pathpoint1) && this.a(pathpoint3) && this.a(pathpoint6) && this.a(pathpoint11) && this.a(pathpoint13)) {
            apathpoint[i++] = pathpoint24;
        }

        PathPoint pathpoint25 = this.a(pathpoint.x - 1, pathpoint.y - 1, pathpoint.z - 1);

        if (this.b(pathpoint25) && this.a(pathpoint17) && this.a(pathpoint4) && this.a(pathpoint2) && this.a(pathpoint6) && this.a(pathpoint14) && this.a(pathpoint12)) {
            apathpoint[i++] = pathpoint25;
        }

        PathPoint pathpoint26 = this.a(pathpoint.x - 1, pathpoint.y - 1, pathpoint.z + 1);

        if (this.b(pathpoint26) && this.a(pathpoint18) && this.a(pathpoint1) && this.a(pathpoint2) && this.a(pathpoint6) && this.a(pathpoint11) && this.a(pathpoint12)) {
            apathpoint[i++] = pathpoint26;
        }

        return i;
    }

    private boolean a(@Nullable PathPoint pathpoint) {
        return pathpoint != null && pathpoint.costMalus >= 0.0F;
    }

    private boolean b(@Nullable PathPoint pathpoint) {
        return pathpoint != null && !pathpoint.closed;
    }

    @Nullable
    @Override
    protected PathPoint a(int i, int j, int k) {
        PathPoint pathpoint = null;
        PathType pathtype = this.b(i, j, k);
        float f = this.mob.a(pathtype);

        if (f >= 0.0F) {
            pathpoint = super.a(i, j, k);
            pathpoint.type = pathtype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            if (pathtype == PathType.WALKABLE) {
                ++pathpoint.costMalus;
            }
        }

        return pathpoint;
    }

    private PathType b(int i, int j, int k) {
        return (PathType) this.pathTypeByPosCache.computeIfAbsent(BlockPosition.a(i, j, k), (l) -> {
            return this.a(this.level, i, j, k, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, this.e(), this.d());
        });
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);
        PathType pathtype = PathType.BLOCKED;
        BlockPosition blockposition = entityinsentient.getChunkCoordinates();

        pathtype = super.a(iblockaccess, i, j, k, l, i1, j1, flag, flag1, enumset, pathtype, blockposition);
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

        if (pathtype == PathType.OPEN && j >= iblockaccess.getMinBuildHeight() + 1) {
            PathType pathtype1 = b(iblockaccess, blockposition_mutableblockposition.d(i, j - 1, k));

            if (pathtype1 != PathType.DAMAGE_FIRE && pathtype1 != PathType.LAVA) {
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
}

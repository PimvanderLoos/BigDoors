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
    public void prepare(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.prepare(chunkcache, entityinsentient);
        this.pathTypeByPosCache.clear();
        this.oldWaterCost = entityinsentient.getPathfindingMalus(PathType.WATER);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        this.pathTypeByPosCache.clear();
        super.done();
    }

    @Override
    public PathPoint getStart() {
        int i;

        if (this.canFloat() && this.mob.isInWater()) {
            i = this.mob.getBlockY();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.mob.getX(), (double) i, this.mob.getZ());

            for (IBlockData iblockdata = this.level.getBlockState(blockposition_mutableblockposition); iblockdata.is(Blocks.WATER); iblockdata = this.level.getBlockState(blockposition_mutableblockposition)) {
                ++i;
                blockposition_mutableblockposition.set(this.mob.getX(), (double) i, this.mob.getZ());
            }
        } else {
            i = MathHelper.floor(this.mob.getY() + 0.5D);
        }

        BlockPosition blockposition = this.mob.blockPosition();
        PathType pathtype = this.getCachedBlockPathType(blockposition.getX(), i, blockposition.getZ());

        if (this.mob.getPathfindingMalus(pathtype) < 0.0F) {
            Set<BlockPosition> set = ImmutableSet.of(new BlockPosition(this.mob.getBoundingBox().minX, (double) i, this.mob.getBoundingBox().minZ), new BlockPosition(this.mob.getBoundingBox().minX, (double) i, this.mob.getBoundingBox().maxZ), new BlockPosition(this.mob.getBoundingBox().maxX, (double) i, this.mob.getBoundingBox().minZ), new BlockPosition(this.mob.getBoundingBox().maxX, (double) i, this.mob.getBoundingBox().maxZ));
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                PathType pathtype1 = this.getCachedBlockPathType(blockposition.getX(), i, blockposition.getZ());

                if (this.mob.getPathfindingMalus(pathtype1) >= 0.0F) {
                    return super.getNode(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
                }
            }
        }

        return super.getNode(blockposition.getX(), i, blockposition.getZ());
    }

    @Override
    public PathDestination getGoal(double d0, double d1, double d2) {
        return new PathDestination(super.getNode(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
    }

    @Override
    public int getNeighbors(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = 0;
        PathPoint pathpoint1 = this.getNode(pathpoint.x, pathpoint.y, pathpoint.z + 1);

        if (this.isOpen(pathpoint1)) {
            apathpoint[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.getNode(pathpoint.x - 1, pathpoint.y, pathpoint.z);

        if (this.isOpen(pathpoint2)) {
            apathpoint[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.getNode(pathpoint.x + 1, pathpoint.y, pathpoint.z);

        if (this.isOpen(pathpoint3)) {
            apathpoint[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.getNode(pathpoint.x, pathpoint.y, pathpoint.z - 1);

        if (this.isOpen(pathpoint4)) {
            apathpoint[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.getNode(pathpoint.x, pathpoint.y + 1, pathpoint.z);

        if (this.isOpen(pathpoint5)) {
            apathpoint[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.getNode(pathpoint.x, pathpoint.y - 1, pathpoint.z);

        if (this.isOpen(pathpoint6)) {
            apathpoint[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.getNode(pathpoint.x, pathpoint.y + 1, pathpoint.z + 1);

        if (this.isOpen(pathpoint7) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint5)) {
            apathpoint[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = this.getNode(pathpoint.x - 1, pathpoint.y + 1, pathpoint.z);

        if (this.isOpen(pathpoint8) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint5)) {
            apathpoint[i++] = pathpoint8;
        }

        PathPoint pathpoint9 = this.getNode(pathpoint.x + 1, pathpoint.y + 1, pathpoint.z);

        if (this.isOpen(pathpoint9) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint5)) {
            apathpoint[i++] = pathpoint9;
        }

        PathPoint pathpoint10 = this.getNode(pathpoint.x, pathpoint.y + 1, pathpoint.z - 1);

        if (this.isOpen(pathpoint10) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint5)) {
            apathpoint[i++] = pathpoint10;
        }

        PathPoint pathpoint11 = this.getNode(pathpoint.x, pathpoint.y - 1, pathpoint.z + 1);

        if (this.isOpen(pathpoint11) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint6)) {
            apathpoint[i++] = pathpoint11;
        }

        PathPoint pathpoint12 = this.getNode(pathpoint.x - 1, pathpoint.y - 1, pathpoint.z);

        if (this.isOpen(pathpoint12) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint6)) {
            apathpoint[i++] = pathpoint12;
        }

        PathPoint pathpoint13 = this.getNode(pathpoint.x + 1, pathpoint.y - 1, pathpoint.z);

        if (this.isOpen(pathpoint13) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint6)) {
            apathpoint[i++] = pathpoint13;
        }

        PathPoint pathpoint14 = this.getNode(pathpoint.x, pathpoint.y - 1, pathpoint.z - 1);

        if (this.isOpen(pathpoint14) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint6)) {
            apathpoint[i++] = pathpoint14;
        }

        PathPoint pathpoint15 = this.getNode(pathpoint.x + 1, pathpoint.y, pathpoint.z - 1);

        if (this.isOpen(pathpoint15) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint3)) {
            apathpoint[i++] = pathpoint15;
        }

        PathPoint pathpoint16 = this.getNode(pathpoint.x + 1, pathpoint.y, pathpoint.z + 1);

        if (this.isOpen(pathpoint16) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint3)) {
            apathpoint[i++] = pathpoint16;
        }

        PathPoint pathpoint17 = this.getNode(pathpoint.x - 1, pathpoint.y, pathpoint.z - 1);

        if (this.isOpen(pathpoint17) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint2)) {
            apathpoint[i++] = pathpoint17;
        }

        PathPoint pathpoint18 = this.getNode(pathpoint.x - 1, pathpoint.y, pathpoint.z + 1);

        if (this.isOpen(pathpoint18) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint2)) {
            apathpoint[i++] = pathpoint18;
        }

        PathPoint pathpoint19 = this.getNode(pathpoint.x + 1, pathpoint.y + 1, pathpoint.z - 1);

        if (this.isOpen(pathpoint19) && this.hasMalus(pathpoint15) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint10) && this.hasMalus(pathpoint9)) {
            apathpoint[i++] = pathpoint19;
        }

        PathPoint pathpoint20 = this.getNode(pathpoint.x + 1, pathpoint.y + 1, pathpoint.z + 1);

        if (this.isOpen(pathpoint20) && this.hasMalus(pathpoint16) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint7) && this.hasMalus(pathpoint9)) {
            apathpoint[i++] = pathpoint20;
        }

        PathPoint pathpoint21 = this.getNode(pathpoint.x - 1, pathpoint.y + 1, pathpoint.z - 1);

        if (this.isOpen(pathpoint21) && this.hasMalus(pathpoint17) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint10) && this.hasMalus(pathpoint8)) {
            apathpoint[i++] = pathpoint21;
        }

        PathPoint pathpoint22 = this.getNode(pathpoint.x - 1, pathpoint.y + 1, pathpoint.z + 1);

        if (this.isOpen(pathpoint22) && this.hasMalus(pathpoint18) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint5) && this.hasMalus(pathpoint7) && this.hasMalus(pathpoint8)) {
            apathpoint[i++] = pathpoint22;
        }

        PathPoint pathpoint23 = this.getNode(pathpoint.x + 1, pathpoint.y - 1, pathpoint.z - 1);

        if (this.isOpen(pathpoint23) && this.hasMalus(pathpoint15) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint6) && this.hasMalus(pathpoint14) && this.hasMalus(pathpoint13)) {
            apathpoint[i++] = pathpoint23;
        }

        PathPoint pathpoint24 = this.getNode(pathpoint.x + 1, pathpoint.y - 1, pathpoint.z + 1);

        if (this.isOpen(pathpoint24) && this.hasMalus(pathpoint16) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint3) && this.hasMalus(pathpoint6) && this.hasMalus(pathpoint11) && this.hasMalus(pathpoint13)) {
            apathpoint[i++] = pathpoint24;
        }

        PathPoint pathpoint25 = this.getNode(pathpoint.x - 1, pathpoint.y - 1, pathpoint.z - 1);

        if (this.isOpen(pathpoint25) && this.hasMalus(pathpoint17) && this.hasMalus(pathpoint4) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint6) && this.hasMalus(pathpoint14) && this.hasMalus(pathpoint12)) {
            apathpoint[i++] = pathpoint25;
        }

        PathPoint pathpoint26 = this.getNode(pathpoint.x - 1, pathpoint.y - 1, pathpoint.z + 1);

        if (this.isOpen(pathpoint26) && this.hasMalus(pathpoint18) && this.hasMalus(pathpoint1) && this.hasMalus(pathpoint2) && this.hasMalus(pathpoint6) && this.hasMalus(pathpoint11) && this.hasMalus(pathpoint12)) {
            apathpoint[i++] = pathpoint26;
        }

        return i;
    }

    private boolean hasMalus(@Nullable PathPoint pathpoint) {
        return pathpoint != null && pathpoint.costMalus >= 0.0F;
    }

    private boolean isOpen(@Nullable PathPoint pathpoint) {
        return pathpoint != null && !pathpoint.closed;
    }

    @Nullable
    @Override
    protected PathPoint getNode(int i, int j, int k) {
        PathPoint pathpoint = null;
        PathType pathtype = this.getCachedBlockPathType(i, j, k);
        float f = this.mob.getPathfindingMalus(pathtype);

        if (f >= 0.0F) {
            pathpoint = super.getNode(i, j, k);
            pathpoint.type = pathtype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            if (pathtype == PathType.WALKABLE) {
                ++pathpoint.costMalus;
            }
        }

        return pathpoint;
    }

    private PathType getCachedBlockPathType(int i, int j, int k) {
        return (PathType) this.pathTypeByPosCache.computeIfAbsent(BlockPosition.asLong(i, j, k), (l) -> {
            return this.getBlockPathType(this.level, i, j, k, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
        });
    }

    @Override
    public PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);
        PathType pathtype = PathType.BLOCKED;
        BlockPosition blockposition = entityinsentient.blockPosition();

        pathtype = super.getBlockPathTypes(iblockaccess, i, j, k, l, i1, j1, flag, flag1, enumset, pathtype, blockposition);
        if (enumset.contains(PathType.FENCE)) {
            return PathType.FENCE;
        } else {
            PathType pathtype1 = PathType.BLOCKED;
            Iterator iterator = enumset.iterator();

            while (iterator.hasNext()) {
                PathType pathtype2 = (PathType) iterator.next();

                if (entityinsentient.getPathfindingMalus(pathtype2) < 0.0F) {
                    return pathtype2;
                }

                if (entityinsentient.getPathfindingMalus(pathtype2) >= entityinsentient.getPathfindingMalus(pathtype1)) {
                    pathtype1 = pathtype2;
                }
            }

            if (pathtype == PathType.OPEN && entityinsentient.getPathfindingMalus(pathtype1) == 0.0F) {
                return PathType.OPEN;
            } else {
                return pathtype1;
            }
        }
    }

    @Override
    public PathType getBlockPathType(IBlockAccess iblockaccess, int i, int j, int k) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        PathType pathtype = getBlockPathTypeRaw(iblockaccess, blockposition_mutableblockposition.set(i, j, k));

        if (pathtype == PathType.OPEN && j >= iblockaccess.getMinBuildHeight() + 1) {
            PathType pathtype1 = getBlockPathTypeRaw(iblockaccess, blockposition_mutableblockposition.set(i, j - 1, k));

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
            pathtype = checkNeighbourBlocks(iblockaccess, blockposition_mutableblockposition.set(i, j, k), pathtype);
        }

        return pathtype;
    }
}

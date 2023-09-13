package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.BlockFenceGate;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathfinderNormal extends PathfinderAbstract {

    public static final double SPACE_BETWEEN_WALL_POSTS = 0.5D;
    protected float oldWaterCost;
    private final Long2ObjectMap<PathType> pathTypesByPosCache = new Long2ObjectOpenHashMap();
    private final Object2BooleanMap<AxisAlignedBB> collisionCache = new Object2BooleanOpenHashMap();

    public PathfinderNormal() {}

    @Override
    public void a(ChunkCache chunkcache, EntityInsentient entityinsentient) {
        super.a(chunkcache, entityinsentient);
        this.oldWaterCost = entityinsentient.a(PathType.WATER);
    }

    @Override
    public void a() {
        this.mob.a(PathType.WATER, this.oldWaterCost);
        this.pathTypesByPosCache.clear();
        this.collisionCache.clear();
        super.a();
    }

    @Override
    public PathPoint b() {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = this.mob.cY();
        IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition.c(this.mob.locX(), (double) i, this.mob.locZ()));
        BlockPosition blockposition;

        if (this.mob.a(iblockdata.getFluid().getType())) {
            while (this.mob.a(iblockdata.getFluid().getType())) {
                ++i;
                iblockdata = this.level.getType(blockposition_mutableblockposition.c(this.mob.locX(), (double) i, this.mob.locZ()));
            }

            --i;
        } else if (this.f() && this.mob.isInWater()) {
            while (iblockdata.a(Blocks.WATER) || iblockdata.getFluid() == FluidTypes.WATER.a(false)) {
                ++i;
                iblockdata = this.level.getType(blockposition_mutableblockposition.c(this.mob.locX(), (double) i, this.mob.locZ()));
            }

            --i;
        } else if (this.mob.isOnGround()) {
            i = MathHelper.floor(this.mob.locY() + 0.5D);
        } else {
            for (blockposition = this.mob.getChunkCoordinates(); (this.level.getType(blockposition).isAir() || this.level.getType(blockposition).a((IBlockAccess) this.level, blockposition, PathMode.LAND)) && blockposition.getY() > this.mob.level.getMinBuildHeight(); blockposition = blockposition.down()) {
                ;
            }

            i = blockposition.up().getY();
        }

        blockposition = this.mob.getChunkCoordinates();
        PathType pathtype = this.a(this.mob, blockposition.getX(), i, blockposition.getZ());

        if (this.mob.a(pathtype) < 0.0F) {
            AxisAlignedBB axisalignedbb = this.mob.getBoundingBox();

            if (this.c(blockposition_mutableblockposition.c(axisalignedbb.minX, (double) i, axisalignedbb.minZ)) || this.c(blockposition_mutableblockposition.c(axisalignedbb.minX, (double) i, axisalignedbb.maxZ)) || this.c(blockposition_mutableblockposition.c(axisalignedbb.maxX, (double) i, axisalignedbb.minZ)) || this.c(blockposition_mutableblockposition.c(axisalignedbb.maxX, (double) i, axisalignedbb.maxZ))) {
                PathPoint pathpoint = this.b(blockposition_mutableblockposition);

                pathpoint.type = this.a(this.mob, pathpoint.a());
                pathpoint.costMalus = this.mob.a(pathpoint.type);
                return pathpoint;
            }
        }

        PathPoint pathpoint1 = this.a(blockposition.getX(), i, blockposition.getZ());

        pathpoint1.type = this.a(this.mob, pathpoint1.a());
        pathpoint1.costMalus = this.mob.a(pathpoint1.type);
        return pathpoint1;
    }

    private boolean c(BlockPosition blockposition) {
        PathType pathtype = this.a(this.mob, blockposition);

        return this.mob.a(pathtype) >= 0.0F;
    }

    @Override
    public PathDestination a(double d0, double d1, double d2) {
        return new PathDestination(this.a(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
    }

    @Override
    public int a(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = 0;
        int j = 0;
        PathType pathtype = this.a(this.mob, pathpoint.x, pathpoint.y + 1, pathpoint.z);
        PathType pathtype1 = this.a(this.mob, pathpoint.x, pathpoint.y, pathpoint.z);

        if (this.mob.a(pathtype) >= 0.0F && pathtype1 != PathType.STICKY_HONEY) {
            j = MathHelper.d(Math.max(1.0F, this.mob.maxUpStep));
        }

        double d0 = this.a(new BlockPosition(pathpoint.x, pathpoint.y, pathpoint.z));
        PathPoint pathpoint1 = this.a(pathpoint.x, pathpoint.y, pathpoint.z + 1, j, d0, EnumDirection.SOUTH, pathtype1);

        if (this.a(pathpoint1, pathpoint)) {
            apathpoint[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.a(pathpoint.x - 1, pathpoint.y, pathpoint.z, j, d0, EnumDirection.WEST, pathtype1);

        if (this.a(pathpoint2, pathpoint)) {
            apathpoint[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.a(pathpoint.x + 1, pathpoint.y, pathpoint.z, j, d0, EnumDirection.EAST, pathtype1);

        if (this.a(pathpoint3, pathpoint)) {
            apathpoint[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.a(pathpoint.x, pathpoint.y, pathpoint.z - 1, j, d0, EnumDirection.NORTH, pathtype1);

        if (this.a(pathpoint4, pathpoint)) {
            apathpoint[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.a(pathpoint.x - 1, pathpoint.y, pathpoint.z - 1, j, d0, EnumDirection.NORTH, pathtype1);

        if (this.a(pathpoint, pathpoint2, pathpoint4, pathpoint5)) {
            apathpoint[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.a(pathpoint.x + 1, pathpoint.y, pathpoint.z - 1, j, d0, EnumDirection.NORTH, pathtype1);

        if (this.a(pathpoint, pathpoint3, pathpoint4, pathpoint6)) {
            apathpoint[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.a(pathpoint.x - 1, pathpoint.y, pathpoint.z + 1, j, d0, EnumDirection.SOUTH, pathtype1);

        if (this.a(pathpoint, pathpoint2, pathpoint1, pathpoint7)) {
            apathpoint[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = this.a(pathpoint.x + 1, pathpoint.y, pathpoint.z + 1, j, d0, EnumDirection.SOUTH, pathtype1);

        if (this.a(pathpoint, pathpoint3, pathpoint1, pathpoint8)) {
            apathpoint[i++] = pathpoint8;
        }

        return i;
    }

    protected boolean a(@Nullable PathPoint pathpoint, PathPoint pathpoint1) {
        return pathpoint != null && !pathpoint.closed && (pathpoint.costMalus >= 0.0F || pathpoint1.costMalus < 0.0F);
    }

    protected boolean a(PathPoint pathpoint, @Nullable PathPoint pathpoint1, @Nullable PathPoint pathpoint2, @Nullable PathPoint pathpoint3) {
        if (pathpoint3 != null && pathpoint2 != null && pathpoint1 != null) {
            if (pathpoint3.closed) {
                return false;
            } else if (pathpoint2.y <= pathpoint.y && pathpoint1.y <= pathpoint.y) {
                if (pathpoint1.type != PathType.WALKABLE_DOOR && pathpoint2.type != PathType.WALKABLE_DOOR && pathpoint3.type != PathType.WALKABLE_DOOR) {
                    boolean flag = pathpoint2.type == PathType.FENCE && pathpoint1.type == PathType.FENCE && (double) this.mob.getWidth() < 0.5D;

                    return pathpoint3.costMalus >= 0.0F && (pathpoint2.y < pathpoint.y || pathpoint2.costMalus >= 0.0F || flag) && (pathpoint1.y < pathpoint.y || pathpoint1.costMalus >= 0.0F || flag);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean a(PathPoint pathpoint) {
        Vec3D vec3d = new Vec3D((double) pathpoint.x - this.mob.locX(), (double) pathpoint.y - this.mob.locY(), (double) pathpoint.z - this.mob.locZ());
        AxisAlignedBB axisalignedbb = this.mob.getBoundingBox();
        int i = MathHelper.e(vec3d.f() / axisalignedbb.a());

        vec3d = vec3d.a((double) (1.0F / (float) i));

        for (int j = 1; j <= i; ++j) {
            axisalignedbb = axisalignedbb.c(vec3d);
            if (this.a(axisalignedbb)) {
                return false;
            }
        }

        return true;
    }

    protected double a(BlockPosition blockposition) {
        return a((IBlockAccess) this.level, blockposition);
    }

    public static double a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        VoxelShape voxelshape = iblockaccess.getType(blockposition1).getCollisionShape(iblockaccess, blockposition1);

        return (double) blockposition1.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.c(EnumDirection.EnumAxis.Y));
    }

    protected boolean c() {
        return false;
    }

    @Nullable
    protected PathPoint a(int i, int j, int k, int l, double d0, EnumDirection enumdirection, PathType pathtype) {
        PathPoint pathpoint = null;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        double d1 = this.a((BlockPosition) blockposition_mutableblockposition.d(i, j, k));

        if (d1 - d0 > 1.125D) {
            return null;
        } else {
            PathType pathtype1 = this.a(this.mob, i, j, k);
            float f = this.mob.a(pathtype1);
            double d2 = (double) this.mob.getWidth() / 2.0D;

            if (f >= 0.0F) {
                pathpoint = this.a(i, j, k);
                pathpoint.type = pathtype1;
                pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            }

            if (pathtype == PathType.FENCE && pathpoint != null && pathpoint.costMalus >= 0.0F && !this.a(pathpoint)) {
                pathpoint = null;
            }

            if (pathtype1 != PathType.WALKABLE && (!this.c() || pathtype1 != PathType.WATER)) {
                if ((pathpoint == null || pathpoint.costMalus < 0.0F) && l > 0 && pathtype1 != PathType.FENCE && pathtype1 != PathType.UNPASSABLE_RAIL && pathtype1 != PathType.TRAPDOOR && pathtype1 != PathType.POWDER_SNOW) {
                    pathpoint = this.a(i, j + 1, k, l - 1, d0, enumdirection, pathtype);
                    if (pathpoint != null && (pathpoint.type == PathType.OPEN || pathpoint.type == PathType.WALKABLE) && this.mob.getWidth() < 1.0F) {
                        double d3 = (double) (i - enumdirection.getAdjacentX()) + 0.5D;
                        double d4 = (double) (k - enumdirection.getAdjacentZ()) + 0.5D;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB(d3 - d2, a((IBlockAccess) this.level, (BlockPosition) blockposition_mutableblockposition.c(d3, (double) (j + 1), d4)) + 0.001D, d4 - d2, d3 + d2, (double) this.mob.getHeight() + a((IBlockAccess) this.level, (BlockPosition) blockposition_mutableblockposition.c((double) pathpoint.x, (double) pathpoint.y, (double) pathpoint.z)) - 0.002D, d4 + d2);

                        if (this.a(axisalignedbb)) {
                            pathpoint = null;
                        }
                    }
                }

                if (!this.c() && pathtype1 == PathType.WATER && !this.f()) {
                    if (this.a(this.mob, i, j - 1, k) != PathType.WATER) {
                        return pathpoint;
                    }

                    while (j > this.mob.level.getMinBuildHeight()) {
                        --j;
                        pathtype1 = this.a(this.mob, i, j, k);
                        if (pathtype1 != PathType.WATER) {
                            return pathpoint;
                        }

                        pathpoint = this.a(i, j, k);
                        pathpoint.type = pathtype1;
                        pathpoint.costMalus = Math.max(pathpoint.costMalus, this.mob.a(pathtype1));
                    }
                }

                if (pathtype1 == PathType.OPEN) {
                    int i1 = 0;
                    int j1 = j;

                    while (pathtype1 == PathType.OPEN) {
                        --j;
                        PathPoint pathpoint1;

                        if (j < this.mob.level.getMinBuildHeight()) {
                            pathpoint1 = this.a(i, j1, k);
                            pathpoint1.type = PathType.BLOCKED;
                            pathpoint1.costMalus = -1.0F;
                            return pathpoint1;
                        }

                        if (i1++ >= this.mob.ce()) {
                            pathpoint1 = this.a(i, j, k);
                            pathpoint1.type = PathType.BLOCKED;
                            pathpoint1.costMalus = -1.0F;
                            return pathpoint1;
                        }

                        pathtype1 = this.a(this.mob, i, j, k);
                        f = this.mob.a(pathtype1);
                        if (pathtype1 != PathType.OPEN && f >= 0.0F) {
                            pathpoint = this.a(i, j, k);
                            pathpoint.type = pathtype1;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            break;
                        }

                        if (f < 0.0F) {
                            pathpoint1 = this.a(i, j, k);
                            pathpoint1.type = PathType.BLOCKED;
                            pathpoint1.costMalus = -1.0F;
                            return pathpoint1;
                        }
                    }
                }

                if (pathtype1 == PathType.FENCE) {
                    pathpoint = this.a(i, j, k);
                    pathpoint.closed = true;
                    pathpoint.type = pathtype1;
                    pathpoint.costMalus = pathtype1.a();
                }

                return pathpoint;
            } else {
                return pathpoint;
            }
        }
    }

    private boolean a(AxisAlignedBB axisalignedbb) {
        return (Boolean) this.collisionCache.computeIfAbsent(axisalignedbb, (axisalignedbb1) -> {
            return !this.level.getCubes(this.mob, axisalignedbb);
        });
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);
        PathType pathtype = PathType.BLOCKED;
        BlockPosition blockposition = entityinsentient.getChunkCoordinates();

        pathtype = this.a(iblockaccess, i, j, k, l, i1, j1, flag, flag1, enumset, pathtype, blockposition);
        if (enumset.contains(PathType.FENCE)) {
            return PathType.FENCE;
        } else if (enumset.contains(PathType.UNPASSABLE_RAIL)) {
            return PathType.UNPASSABLE_RAIL;
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

            if (pathtype == PathType.OPEN && entityinsentient.a(pathtype1) == 0.0F && l <= 1) {
                return PathType.OPEN;
            } else {
                return pathtype1;
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

                    pathtype1 = this.a(iblockaccess, flag, flag1, blockposition, pathtype1);
                    if (k1 == 0 && l1 == 0 && i2 == 0) {
                        pathtype = pathtype1;
                    }

                    enumset.add(pathtype1);
                }
            }
        }

        return pathtype;
    }

    protected PathType a(IBlockAccess iblockaccess, boolean flag, boolean flag1, BlockPosition blockposition, PathType pathtype) {
        if (pathtype == PathType.DOOR_WOOD_CLOSED && flag && flag1) {
            pathtype = PathType.WALKABLE_DOOR;
        }

        if (pathtype == PathType.DOOR_OPEN && !flag1) {
            pathtype = PathType.BLOCKED;
        }

        if (pathtype == PathType.RAIL && !(iblockaccess.getType(blockposition).getBlock() instanceof BlockMinecartTrackAbstract) && !(iblockaccess.getType(blockposition.down()).getBlock() instanceof BlockMinecartTrackAbstract)) {
            pathtype = PathType.UNPASSABLE_RAIL;
        }

        if (pathtype == PathType.LEAVES) {
            pathtype = PathType.BLOCKED;
        }

        return pathtype;
    }

    private PathType a(EntityInsentient entityinsentient, BlockPosition blockposition) {
        return this.a(entityinsentient, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    protected PathType a(EntityInsentient entityinsentient, int i, int j, int k) {
        return (PathType) this.pathTypesByPosCache.computeIfAbsent(BlockPosition.a(i, j, k), (l) -> {
            return this.a(this.level, i, j, k, entityinsentient, this.entityWidth, this.entityHeight, this.entityDepth, this.e(), this.d());
        });
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        return a(iblockaccess, new BlockPosition.MutableBlockPosition(i, j, k));
    }

    public static PathType a(IBlockAccess iblockaccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        int i = blockposition_mutableblockposition.getX();
        int j = blockposition_mutableblockposition.getY();
        int k = blockposition_mutableblockposition.getZ();
        PathType pathtype = b(iblockaccess, blockposition_mutableblockposition);

        if (pathtype == PathType.OPEN && j >= iblockaccess.getMinBuildHeight() + 1) {
            PathType pathtype1 = b(iblockaccess, blockposition_mutableblockposition.d(i, j - 1, k));

            pathtype = pathtype1 != PathType.WALKABLE && pathtype1 != PathType.OPEN && pathtype1 != PathType.WATER && pathtype1 != PathType.LAVA ? PathType.WALKABLE : PathType.OPEN;
            if (pathtype1 == PathType.DAMAGE_FIRE) {
                pathtype = PathType.DAMAGE_FIRE;
            }

            if (pathtype1 == PathType.DAMAGE_CACTUS) {
                pathtype = PathType.DAMAGE_CACTUS;
            }

            if (pathtype1 == PathType.DAMAGE_OTHER) {
                pathtype = PathType.DAMAGE_OTHER;
            }

            if (pathtype1 == PathType.STICKY_HONEY) {
                pathtype = PathType.STICKY_HONEY;
            }
        }

        if (pathtype == PathType.WALKABLE) {
            pathtype = a(iblockaccess, blockposition_mutableblockposition.d(i, j, k), pathtype);
        }

        return pathtype;
    }

    public static PathType a(IBlockAccess iblockaccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, PathType pathtype) {
        int i = blockposition_mutableblockposition.getX();
        int j = blockposition_mutableblockposition.getY();
        int k = blockposition_mutableblockposition.getZ();

        for (int l = -1; l <= 1; ++l) {
            for (int i1 = -1; i1 <= 1; ++i1) {
                for (int j1 = -1; j1 <= 1; ++j1) {
                    if (l != 0 || j1 != 0) {
                        blockposition_mutableblockposition.d(i + l, j + i1, k + j1);
                        IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

                        if (iblockdata.a(Blocks.CACTUS)) {
                            return PathType.DANGER_CACTUS;
                        }

                        if (iblockdata.a(Blocks.SWEET_BERRY_BUSH)) {
                            return PathType.DANGER_OTHER;
                        }

                        if (a(iblockdata)) {
                            return PathType.DANGER_FIRE;
                        }

                        if (iblockaccess.getFluid(blockposition_mutableblockposition).a((Tag) TagsFluid.WATER)) {
                            return PathType.WATER_BORDER;
                        }
                    }
                }
            }
        }

        return pathtype;
    }

    protected static PathType b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        Block block = iblockdata.getBlock();
        Material material = iblockdata.getMaterial();

        if (iblockdata.isAir()) {
            return PathType.OPEN;
        } else if (!iblockdata.a((Tag) TagsBlock.TRAPDOORS) && !iblockdata.a(Blocks.LILY_PAD) && !iblockdata.a(Blocks.BIG_DRIPLEAF)) {
            if (iblockdata.a(Blocks.POWDER_SNOW)) {
                return PathType.POWDER_SNOW;
            } else if (iblockdata.a(Blocks.CACTUS)) {
                return PathType.DAMAGE_CACTUS;
            } else if (iblockdata.a(Blocks.SWEET_BERRY_BUSH)) {
                return PathType.DAMAGE_OTHER;
            } else if (iblockdata.a(Blocks.HONEY_BLOCK)) {
                return PathType.STICKY_HONEY;
            } else if (iblockdata.a(Blocks.COCOA)) {
                return PathType.COCOA;
            } else {
                Fluid fluid = iblockaccess.getFluid(blockposition);

                return fluid.a((Tag) TagsFluid.LAVA) ? PathType.LAVA : (a(iblockdata) ? PathType.DAMAGE_FIRE : (BlockDoor.n(iblockdata) && !(Boolean) iblockdata.get(BlockDoor.OPEN) ? PathType.DOOR_WOOD_CLOSED : (block instanceof BlockDoor && material == Material.METAL && !(Boolean) iblockdata.get(BlockDoor.OPEN) ? PathType.DOOR_IRON_CLOSED : (block instanceof BlockDoor && (Boolean) iblockdata.get(BlockDoor.OPEN) ? PathType.DOOR_OPEN : (block instanceof BlockMinecartTrackAbstract ? PathType.RAIL : (block instanceof BlockLeaves ? PathType.LEAVES : (!iblockdata.a((Tag) TagsBlock.FENCES) && !iblockdata.a((Tag) TagsBlock.WALLS) && (!(block instanceof BlockFenceGate) || (Boolean) iblockdata.get(BlockFenceGate.OPEN)) ? (!iblockdata.a(iblockaccess, blockposition, PathMode.LAND) ? PathType.BLOCKED : (fluid.a((Tag) TagsFluid.WATER) ? PathType.WATER : PathType.OPEN)) : PathType.FENCE)))))));
            }
        } else {
            return PathType.TRAPDOOR;
        }
    }

    public static boolean a(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.FIRE) || iblockdata.a(Blocks.LAVA) || iblockdata.a(Blocks.MAGMA_BLOCK) || BlockCampfire.g(iblockdata) || iblockdata.a(Blocks.LAVA_CAULDRON);
    }
}

package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public interface IWorldReader extends IBlockAccess {

    boolean isEmpty(BlockPosition blockposition);

    BiomeBase getBiome(BlockPosition blockposition);

    int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition);

    default boolean z(BlockPosition blockposition) {
        if (blockposition.getY() >= this.getSeaLevel()) {
            return this.e(blockposition);
        } else {
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), this.getSeaLevel(), blockposition.getZ());

            if (!this.e(blockposition1)) {
                return false;
            } else {
                for (blockposition1 = blockposition1.down(); blockposition1.getY() > blockposition.getY(); blockposition1 = blockposition1.down()) {
                    IBlockData iblockdata = this.getType(blockposition1);

                    if (iblockdata.b(this, blockposition1) > 0 && !iblockdata.getMaterial().isLiquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    int getLightLevel(BlockPosition blockposition, int i);

    boolean isChunkLoaded(int i, int j, boolean flag);

    boolean e(BlockPosition blockposition);

    default BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return new BlockPosition(blockposition.getX(), this.a(heightmap_type, blockposition.getX(), blockposition.getZ()), blockposition.getZ());
    }

    int a(HeightMap.Type heightmap_type, int i, int j);

    default float A(BlockPosition blockposition) {
        return this.o().i()[this.getLightLevel(blockposition)];
    }

    @Nullable
    default EntityHuman findNearbyPlayer(Entity entity, double d0) {
        return this.a(entity.locX, entity.locY, entity.locZ, d0, false);
    }

    @Nullable
    default EntityHuman b(Entity entity, double d0) {
        return this.a(entity.locX, entity.locY, entity.locZ, d0, true);
    }

    @Nullable
    default EntityHuman a(double d0, double d1, double d2, double d3, boolean flag) {
        Predicate predicate = flag ? IEntitySelector.d : IEntitySelector.e;

        return this.a(d0, d1, d2, d3, predicate);
    }

    @Nullable
    EntityHuman a(double d0, double d1, double d2, double d3, Predicate<Entity> predicate);

    int c();

    WorldBorder getWorldBorder();

    boolean a(@Nullable Entity entity, VoxelShape voxelshape);

    List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb);

    int a(BlockPosition blockposition, EnumDirection enumdirection);

    boolean e();

    int getSeaLevel();

    default boolean a(IBlockData iblockdata, BlockPosition blockposition) {
        VoxelShape voxelshape = iblockdata.h(this, blockposition);

        return voxelshape.b() || this.a((Entity) null, voxelshape.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
    }

    default boolean b(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return this.a(entity, VoxelShapes.a(axisalignedbb));
    }

    default VoxelShape a(VoxelShape voxelshape, VoxelShape voxelshape1, boolean flag, boolean flag1) {
        int i = MathHelper.floor(voxelshape.b(EnumDirection.EnumAxis.X)) - 1;
        int j = MathHelper.f(voxelshape.c(EnumDirection.EnumAxis.X)) + 1;
        int k = MathHelper.floor(voxelshape.b(EnumDirection.EnumAxis.Y)) - 1;
        int l = MathHelper.f(voxelshape.c(EnumDirection.EnumAxis.Y)) + 1;
        int i1 = MathHelper.floor(voxelshape.b(EnumDirection.EnumAxis.Z)) - 1;
        int j1 = MathHelper.f(voxelshape.c(EnumDirection.EnumAxis.Z)) + 1;
        WorldBorder worldborder = this.getWorldBorder();
        VoxelShape voxelshape2 = VoxelShapes.a();
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(j - i, l - k, j1 - i1);
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                VoxelShape voxelshape3 = VoxelShapes.a();

                for (int l1 = i1; l1 < j1; ++l1) {
                    boolean flag2 = k1 == i || k1 == j - 1;
                    boolean flag3 = l1 == i1 || l1 == j1 - 1;

                    if ((!flag2 || !flag3) && this.isLoaded(blockposition_b.f(k1, 64, l1))) {
                        VoxelShape voxelshape4 = VoxelShapes.a();

                        for (int i2 = k; i2 < l; ++i2) {
                            if (!flag2 && !flag3 || i2 != l - 1) {
                                blockposition_b.f(k1, i2, l1);
                                VoxelShape voxelshape5;

                                if (!worldborder.a((BlockPosition) blockposition_b) && flag1) {
                                    voxelshape5 = VoxelShapes.b();
                                } else {
                                    voxelshape5 = this.getType(blockposition_b).h(this, blockposition_b);
                                }

                                if (!voxelshape5.b()) {
                                    VoxelShape voxelshape6;
                                    VoxelShape voxelshape7;

                                    if (voxelshape5 == VoxelShapes.b()) {
                                        if (flag) {
                                            voxelshape6 = voxelshape5.a((double) k1, (double) i2, (double) l1);
                                            if (!VoxelShapes.c(voxelshape1, voxelshape6, OperatorBoolean.AND) && VoxelShapes.c(voxelshape, voxelshape6, OperatorBoolean.AND)) {
                                                voxelshape7 = VoxelShapes.b();
                                                return voxelshape7;
                                            }
                                        } else {
                                            voxelshapebitset.a(k1 - i, i2 - k, l1 - i1, true, true);
                                        }
                                    } else {
                                        voxelshape6 = voxelshape5.a((double) k1, (double) i2, (double) l1);
                                        if (!VoxelShapes.c(voxelshape1, voxelshape6, OperatorBoolean.AND) && VoxelShapes.c(voxelshape, voxelshape6, OperatorBoolean.AND)) {
                                            if (flag) {
                                                voxelshape7 = VoxelShapes.b();
                                                return voxelshape7;
                                            }

                                            voxelshape4 = VoxelShapes.b(voxelshape4, voxelshape6, OperatorBoolean.OR);
                                        }
                                    }
                                }
                            }
                        }

                        voxelshape3 = VoxelShapes.b(voxelshape3, voxelshape4, OperatorBoolean.OR);
                    }
                }

                voxelshape2 = VoxelShapes.b(voxelshape2, voxelshape3, OperatorBoolean.OR);
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }

        if (flag) {
            return VoxelShapes.a();
        } else {
            voxelshape2 = voxelshape2.c();
            VoxelShapeWorldRegion voxelshapeworldregion = new VoxelShapeWorldRegion(voxelshapebitset, i, k, i1);

            voxelshape2 = VoxelShapes.b(voxelshape2, voxelshapeworldregion, OperatorBoolean.OR);
            return voxelshape2;
        }
    }

    default VoxelShape a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, double d0, double d1, double d2) {
        return this.a(entity, axisalignedbb, Collections.emptySet(), d0, d1, d2);
    }

    default VoxelShape a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set, double d0, double d1, double d2) {
        double d3 = 1.0E-7D;
        VoxelShape voxelshape = VoxelShapes.a(axisalignedbb);
        VoxelShape voxelshape1 = VoxelShapes.a(axisalignedbb.d(d0 > 0.0D ? -1.0E-7D : 1.0E-7D, d1 > 0.0D ? -1.0E-7D : 1.0E-7D, d2 > 0.0D ? -1.0E-7D : 1.0E-7D));
        VoxelShape voxelshape2 = VoxelShapes.b(VoxelShapes.a(axisalignedbb.b(d0, d1, d2).g(1.0E-7D)), voxelshape1, OperatorBoolean.ONLY_FIRST);

        return this.a(entity, voxelshape2, voxelshape, set, false);
    }

    default VoxelShape c(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return this.a(entity, VoxelShapes.a(axisalignedbb), VoxelShapes.a(), Collections.emptySet(), false);
    }

    default VoxelShape a(@Nullable Entity entity, VoxelShape voxelshape, VoxelShape voxelshape1, Set<Entity> set, boolean flag) {
        boolean flag1 = entity != null && entity.bG();
        boolean flag2 = entity != null && this.i(entity);

        if (entity != null && flag1 == flag2) {
            entity.n(!flag2);
        }

        VoxelShape voxelshape2 = this.a(voxelshape, voxelshape1, flag, flag2);

        return (!flag || voxelshape2.b()) && entity != null ? (voxelshape.b() ? VoxelShapes.a() : VoxelShapes.b(voxelshape2, this.a(entity, voxelshape, flag, set), OperatorBoolean.OR)) : voxelshape2;
    }

    default VoxelShape a(@Nullable Entity entity, VoxelShape voxelshape, boolean flag, Set<Entity> set) {
        AxisAlignedBB axisalignedbb = voxelshape.a();
        List list = this.getEntities(entity, axisalignedbb.g(0.25D));
        VoxelShape voxelshape1 = VoxelShapes.a();

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity) list.get(i);

            if (!set.contains(entity1) && !entity.x(entity1)) {
                AxisAlignedBB axisalignedbb1 = entity1.al();

                if (axisalignedbb1 != null && axisalignedbb1.c(axisalignedbb)) {
                    voxelshape1 = VoxelShapes.b(voxelshape1, VoxelShapes.a(axisalignedbb1), OperatorBoolean.OR);
                    if (flag) {
                        break;
                    }
                }

                axisalignedbb1 = entity.j(entity1);
                if (axisalignedbb1 != null && axisalignedbb1.c(axisalignedbb)) {
                    voxelshape1 = VoxelShapes.b(voxelshape1, VoxelShapes.a(axisalignedbb1), OperatorBoolean.OR);
                    if (flag) {
                        break;
                    }
                }
            }
        }

        return voxelshape1;
    }

    default boolean i(Entity entity) {
        WorldBorder worldborder = this.getWorldBorder();
        double d0 = worldborder.b();
        double d1 = worldborder.c();
        double d2 = worldborder.d();
        double d3 = worldborder.e();

        if (entity.bG()) {
            ++d0;
            ++d1;
            --d2;
            --d3;
        } else {
            --d0;
            --d1;
            ++d2;
            ++d3;
        }

        return entity.locX > d0 && entity.locX < d2 && entity.locZ > d1 && entity.locZ < d3;
    }

    default boolean a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return this.a(entity, VoxelShapes.a(axisalignedbb), VoxelShapes.a(), set, true).b();
    }

    default boolean getCubes(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return this.a(entity, VoxelShapes.a(axisalignedbb), VoxelShapes.a(), Collections.emptySet(), true).b();
    }

    default boolean B(BlockPosition blockposition) {
        return this.b(blockposition).a(TagsFluid.a);
    }

    default boolean containsLiquid(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.f(axisalignedbb.d);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.f(axisalignedbb.e);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.f(axisalignedbb.f);
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        IBlockData iblockdata = this.getType(blockposition_b.f(k1, l1, i2));

                        if (!iblockdata.s().e()) {
                            boolean flag = true;

                            return flag;
                        }
                    }
                }
            }

            return false;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }
    }

    default int getLightLevel(BlockPosition blockposition) {
        return this.d(blockposition, this.c());
    }

    default int d(BlockPosition blockposition, int i) {
        if (blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000) {
            if (this.getType(blockposition).c(this, blockposition)) {
                int j = this.getLightLevel(blockposition.up(), i);
                int k = this.getLightLevel(blockposition.east(), i);
                int l = this.getLightLevel(blockposition.west(), i);
                int i1 = this.getLightLevel(blockposition.south(), i);
                int j1 = this.getLightLevel(blockposition.north(), i);

                if (k > j) {
                    j = k;
                }

                if (l > j) {
                    j = l;
                }

                if (i1 > j) {
                    j = i1;
                }

                if (j1 > j) {
                    j = j1;
                }

                return j;
            } else {
                return this.getLightLevel(blockposition, i);
            }
        } else {
            return 15;
        }
    }

    default boolean isLoaded(BlockPosition blockposition) {
        return this.b(blockposition, true);
    }

    default boolean b(BlockPosition blockposition, boolean flag) {
        return this.isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4, flag);
    }

    default boolean areChunksLoaded(BlockPosition blockposition, int i) {
        return this.areChunksLoaded(blockposition, i, true);
    }

    default boolean areChunksLoaded(BlockPosition blockposition, int i, boolean flag) {
        return this.isAreaLoaded(blockposition.getX() - i, blockposition.getY() - i, blockposition.getZ() - i, blockposition.getX() + i, blockposition.getY() + i, blockposition.getZ() + i, flag);
    }

    default boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1) {
        return this.areChunksLoadedBetween(blockposition, blockposition1, true);
    }

    default boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1, boolean flag) {
        return this.isAreaLoaded(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), flag);
    }

    default boolean a(StructureBoundingBox structureboundingbox) {
        return this.a(structureboundingbox, true);
    }

    default boolean a(StructureBoundingBox structureboundingbox, boolean flag) {
        return this.isAreaLoaded(structureboundingbox.a, structureboundingbox.b, structureboundingbox.c, structureboundingbox.d, structureboundingbox.e, structureboundingbox.f, flag);
    }

    default boolean isAreaLoaded(int i, int j, int k, int l, int i1, int j1, boolean flag) {
        if (i1 >= 0 && j < 256) {
            i >>= 4;
            k >>= 4;
            l >>= 4;
            j1 >>= 4;

            for (int k1 = i; k1 <= l; ++k1) {
                for (int l1 = k; l1 <= j1; ++l1) {
                    if (!this.isChunkLoaded(k1, l1, flag)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    WorldProvider o();
}

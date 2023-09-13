package net.minecraft.server;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface ICombinedAccess extends IEntityAccess, IWorldReader, VirtualLevelWritable {

    @Override
    default Stream<VoxelShape> c(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return IEntityAccess.super.c(entity, axisalignedbb, predicate);
    }

    @Override
    default boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        return IEntityAccess.super.a(entity, voxelshape);
    }

    @Override
    default BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return IWorldReader.super.getHighestBlockYAt(heightmap_type, blockposition);
    }

    IRegistryCustom r();

    default Optional<ResourceKey<BiomeBase>> i(BlockPosition blockposition) {
        return this.r().b(IRegistry.ay).c(this.getBiome(blockposition));
    }
}

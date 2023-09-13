package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;

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

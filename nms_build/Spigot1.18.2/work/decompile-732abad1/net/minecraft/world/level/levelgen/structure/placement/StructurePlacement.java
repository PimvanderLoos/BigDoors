package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface StructurePlacement {

    Codec<StructurePlacement> CODEC = IRegistry.STRUCTURE_PLACEMENT_TYPE.byNameCodec().dispatch(StructurePlacement::type, StructurePlacementType::codec);

    boolean isFeatureChunk(ChunkGenerator chunkgenerator, long i, int j, int k);

    StructurePlacementType<?> type();
}

package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WorldGenFeatureDefinedStructurePoolEmpty extends WorldGenFeatureDefinedStructurePoolStructure {

    public static final Codec<WorldGenFeatureDefinedStructurePoolEmpty> a = Codec.unit(() -> {
        return WorldGenFeatureDefinedStructurePoolEmpty.b;
    });
    public static final WorldGenFeatureDefinedStructurePoolEmpty b = new WorldGenFeatureDefinedStructurePoolEmpty();

    private WorldGenFeatureDefinedStructurePoolEmpty() {
        super(WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING);
    }

    @Override
    public List<DefinedStructure.BlockInfo> a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, Random random) {
        return Collections.emptyList();
    }

    @Override
    public StructureBoundingBox a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
        return StructureBoundingBox.a();
    }

    @Override
    public boolean a(DefinedStructureManager definedstructuremanager, GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox, Random random, boolean flag) {
        return true;
    }

    @Override
    public WorldGenFeatureDefinedStructurePools<?> a() {
        return WorldGenFeatureDefinedStructurePools.d;
    }

    public String toString() {
        return "Empty";
    }
}

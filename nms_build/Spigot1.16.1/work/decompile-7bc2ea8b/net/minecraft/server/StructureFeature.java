package net.minecraft.server;

import com.mojang.serialization.Codec;

public class StructureFeature<FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> {

    public static final Codec<StructureFeature<?, ?>> a = IRegistry.STRUCTURE_FEATURE.dispatch("name", (structurefeature) -> {
        return structurefeature.b;
    }, StructureGenerator::h);
    public final F b;
    public final FC c;

    public StructureFeature(F f0, FC fc) {
        this.b = f0;
        this.c = fc;
    }

    public StructureStart<?> a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, int j, StructureSettingsFeature structuresettingsfeature) {
        return this.b.a(chunkgenerator, worldchunkmanager, definedstructuremanager, i, chunkcoordintpair, biomebase, j, new SeededRandom(), structuresettingsfeature, this.c);
    }
}

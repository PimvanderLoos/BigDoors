package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class StructureFeature<FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> {

    public static final Codec<StructureFeature<?, ?>> a = IRegistry.STRUCTURE_FEATURE.dispatch((structurefeature) -> {
        return structurefeature.d;
    }, StructureGenerator::h);
    public static final Codec<Supplier<StructureFeature<?, ?>>> b = RegistryFileCodec.a(IRegistry.av, StructureFeature.a);
    public static final Codec<List<Supplier<StructureFeature<?, ?>>>> c = RegistryFileCodec.b(IRegistry.av, StructureFeature.a);
    public final F d;
    public final FC e;

    public StructureFeature(F f0, FC fc) {
        this.d = f0;
        this.e = fc;
    }

    public StructureStart<?> a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, int j, StructureSettingsFeature structuresettingsfeature) {
        return this.d.a(iregistrycustom, chunkgenerator, worldchunkmanager, definedstructuremanager, i, chunkcoordintpair, biomebase, j, new SeededRandom(), structuresettingsfeature, this.e);
    }
}

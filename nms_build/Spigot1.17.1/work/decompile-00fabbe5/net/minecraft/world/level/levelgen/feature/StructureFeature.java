package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class StructureFeature<FC extends WorldGenFeatureConfiguration, F extends StructureGenerator<FC>> {

    public static final Codec<StructureFeature<?, ?>> DIRECT_CODEC = IRegistry.STRUCTURE_FEATURE.dispatch((structurefeature) -> {
        return structurefeature.feature;
    }, StructureGenerator::f);
    public static final Codec<Supplier<StructureFeature<?, ?>>> CODEC = RegistryFileCodec.a(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
    public static final Codec<List<Supplier<StructureFeature<?, ?>>>> LIST_CODEC = RegistryFileCodec.b(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeature.DIRECT_CODEC);
    public final F feature;
    public final FC config;

    public StructureFeature(F f0, FC fc) {
        this.feature = f0;
        this.config = fc;
    }

    public StructureStart<?> a(IRegistryCustom iregistrycustom, ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, DefinedStructureManager definedstructuremanager, long i, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, int j, StructureSettingsFeature structuresettingsfeature, LevelHeightAccessor levelheightaccessor) {
        return this.feature.a(iregistrycustom, chunkgenerator, worldchunkmanager, definedstructuremanager, i, chunkcoordintpair, biomebase, j, new SeededRandom(), structuresettingsfeature, this.config, levelheightaccessor);
    }
}

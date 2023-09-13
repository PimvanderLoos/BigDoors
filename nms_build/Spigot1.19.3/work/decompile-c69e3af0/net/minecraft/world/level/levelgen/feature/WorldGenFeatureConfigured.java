package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public record WorldGenFeatureConfigured<FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> (F feature, FC config) {

    public static final Codec<WorldGenFeatureConfigured<?, ?>> DIRECT_CODEC = BuiltInRegistries.FEATURE.byNameCodec().dispatch((worldgenfeatureconfigured) -> {
        return worldgenfeatureconfigured.feature;
    }, WorldGenerator::configuredCodec);
    public static final Codec<Holder<WorldGenFeatureConfigured<?, ?>>> CODEC = RegistryFileCodec.create(Registries.CONFIGURED_FEATURE, WorldGenFeatureConfigured.DIRECT_CODEC);
    public static final Codec<HolderSet<WorldGenFeatureConfigured<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.CONFIGURED_FEATURE, WorldGenFeatureConfigured.DIRECT_CODEC);

    public boolean place(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition) {
        return this.feature.place(this.config, generatoraccessseed, chunkgenerator, randomsource, blockposition);
    }

    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return Stream.concat(Stream.of(this), this.config.getFeatures());
    }

    public String toString() {
        return "Configured: " + this.feature + ": " + this.config;
    }
}

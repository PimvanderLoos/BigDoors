package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public class FeaturePlaceContext<FC extends WorldGenFeatureConfiguration> {

    private final Optional<WorldGenFeatureConfigured<?, ?>> topFeature;
    private final GeneratorAccessSeed level;
    private final ChunkGenerator chunkGenerator;
    private final RandomSource random;
    private final BlockPosition origin;
    private final FC config;

    public FeaturePlaceContext(Optional<WorldGenFeatureConfigured<?, ?>> optional, GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition, FC fc) {
        this.topFeature = optional;
        this.level = generatoraccessseed;
        this.chunkGenerator = chunkgenerator;
        this.random = randomsource;
        this.origin = blockposition;
        this.config = fc;
    }

    public Optional<WorldGenFeatureConfigured<?, ?>> topFeature() {
        return this.topFeature;
    }

    public GeneratorAccessSeed level() {
        return this.level;
    }

    public ChunkGenerator chunkGenerator() {
        return this.chunkGenerator;
    }

    public RandomSource random() {
        return this.random;
    }

    public BlockPosition origin() {
        return this.origin;
    }

    public FC config() {
        return this.config;
    }
}

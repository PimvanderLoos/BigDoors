package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public class FeaturePlaceContext<FC extends WorldGenFeatureConfiguration> {

    private final Optional<WorldGenFeatureConfigured<?, ?>> topFeature;
    private final GeneratorAccessSeed level;
    private final ChunkGenerator chunkGenerator;
    private final Random random;
    private final BlockPosition origin;
    private final FC config;

    public FeaturePlaceContext(Optional<WorldGenFeatureConfigured<?, ?>> optional, GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, FC fc) {
        this.topFeature = optional;
        this.level = generatoraccessseed;
        this.chunkGenerator = chunkgenerator;
        this.random = random;
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

    public Random random() {
        return this.random;
    }

    public BlockPosition origin() {
        return this.origin;
    }

    public FC config() {
        return this.config;
    }
}

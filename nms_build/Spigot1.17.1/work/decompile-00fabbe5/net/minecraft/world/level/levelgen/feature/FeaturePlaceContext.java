package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public class FeaturePlaceContext<FC extends WorldGenFeatureConfiguration> {

    private final GeneratorAccessSeed level;
    private final ChunkGenerator chunkGenerator;
    private final Random random;
    private final BlockPosition origin;
    private final FC config;

    public FeaturePlaceContext(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, FC fc) {
        this.level = generatoraccessseed;
        this.chunkGenerator = chunkgenerator;
        this.random = random;
        this.origin = blockposition;
        this.config = fc;
    }

    public GeneratorAccessSeed a() {
        return this.level;
    }

    public ChunkGenerator b() {
        return this.chunkGenerator;
    }

    public Random c() {
        return this.random;
    }

    public BlockPosition d() {
        return this.origin;
    }

    public FC e() {
        return this.config;
    }
}

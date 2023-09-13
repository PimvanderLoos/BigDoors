package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureRandom extends WorldGenerator<WorldGenFeatureRandomConfiguration> {

    public WorldGenFeatureRandom() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandomConfiguration worldgenfeaturerandomconfiguration) {
        int i = random.nextInt(5) - 3 + worldgenfeaturerandomconfiguration.c;

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(worldgenfeaturerandomconfiguration.a.length);

            this.a(worldgenfeaturerandomconfiguration.a[k], worldgenfeaturerandomconfiguration.b[k], generatoraccess, chunkgenerator, random, blockposition);
        }

        return true;
    }

    <FC extends WorldGenFeatureConfiguration> boolean a(WorldGenerator<FC> worldgenerator, WorldGenFeatureConfiguration worldgenfeatureconfiguration, GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition) {
        return worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition, worldgenfeatureconfiguration);
    }
}

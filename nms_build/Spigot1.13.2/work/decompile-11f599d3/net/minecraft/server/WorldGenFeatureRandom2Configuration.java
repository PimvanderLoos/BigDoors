package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureRandom2Configuration extends WorldGenerator<WorldGenFeatureRandom2> {

    public WorldGenFeatureRandom2Configuration() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandom2 worldgenfeaturerandom2) {
        int i = random.nextInt(worldgenfeaturerandom2.a.length);

        return this.a(worldgenfeaturerandom2.a[i], worldgenfeaturerandom2.b[i], generatoraccess, chunkgenerator, random, blockposition);
    }

    <FC extends WorldGenFeatureConfiguration> boolean a(WorldGenerator<FC> worldgenerator, WorldGenFeatureConfiguration worldgenfeatureconfiguration, GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition) {
        return worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition, worldgenfeatureconfiguration);
    }
}

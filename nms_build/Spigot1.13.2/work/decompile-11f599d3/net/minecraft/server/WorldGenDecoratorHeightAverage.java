package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorHeightAverage extends WorldGenDecorator<WorldGenDecoratorHeightAverageConfiguration> {

    public WorldGenDecoratorHeightAverage() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorHeightAverageConfiguration worldgendecoratorheightaverageconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = worldgendecoratorheightaverageconfiguration.a;
        int j = worldgendecoratorheightaverageconfiguration.b;
        int k = worldgendecoratorheightaverageconfiguration.c;

        for (int l = 0; l < i; ++l) {
            int i1 = random.nextInt(16);
            int j1 = random.nextInt(k) + random.nextInt(k) - k + j;
            int k1 = random.nextInt(16);
            BlockPosition blockposition1 = blockposition.a(i1, j1, k1);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition1, c0);
        }

        return true;
    }
}

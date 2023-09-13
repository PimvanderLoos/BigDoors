package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorLakeWater extends WorldGenDecorator<WorldGenDecoratorLakeChanceConfiguration> {

    public WorldGenDecoratorLakeWater() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorLakeChanceConfiguration worldgendecoratorlakechanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        if (random.nextInt(worldgendecoratorlakechanceconfiguration.a) == 0) {
            int i = random.nextInt(16);
            int j = random.nextInt(chunkgenerator.getGenerationDepth());
            int k = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(i, j, k), c0);
        }

        return true;
    }
}

package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorLakeLava extends WorldGenDecorator<WorldGenDecoratorLakeChanceConfiguration> {

    public WorldGenDecoratorLakeLava() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorLakeChanceConfiguration worldgendecoratorlakechanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        if (random.nextInt(worldgendecoratorlakechanceconfiguration.a / 10) == 0) {
            int i = random.nextInt(16);
            int j = random.nextInt(random.nextInt(chunkgenerator.getGenerationDepth() - 8) + 8);
            int k = random.nextInt(16);

            if (j < generatoraccess.getSeaLevel() || random.nextInt(worldgendecoratorlakechanceconfiguration.a / 8) == 0) {
                worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(i, j, k), c0);
            }
        }

        return true;
    }
}

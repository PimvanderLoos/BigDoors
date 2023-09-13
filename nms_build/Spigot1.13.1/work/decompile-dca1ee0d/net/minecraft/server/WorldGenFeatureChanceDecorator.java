package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureChanceDecorator extends WorldGenDecorator<WorldGenDecoratorFrequencyChanceConfiguration> {

    public WorldGenFeatureChanceDecorator() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyChanceConfiguration worldgendecoratorfrequencychanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgendecoratorfrequencychanceconfiguration.a; ++i) {
            if (random.nextFloat() < worldgendecoratorfrequencychanceconfiguration.b) {
                int j = random.nextInt(16);
                int k = random.nextInt(16);
                BlockPosition blockposition1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(j, 0, k));

                worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition1, c0);
            }
        }

        return true;
    }
}

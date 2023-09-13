package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureChanceDecoratorHeight extends WorldGenDecorator<WorldGenDecoratorFrequencyChanceConfiguration> {

    public WorldGenFeatureChanceDecoratorHeight() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyChanceConfiguration worldgendecoratorfrequencychanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgendecoratorfrequencychanceconfiguration.a; ++i) {
            if (random.nextFloat() < worldgendecoratorfrequencychanceconfiguration.b) {
                int j = random.nextInt(16);
                int k = random.nextInt(16);
                int l = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(j, 0, k)).getY() * 2;

                if (l > 0) {
                    int i1 = random.nextInt(l);

                    worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(j, i1, k), c0);
                }
            }
        }

        return true;
    }
}

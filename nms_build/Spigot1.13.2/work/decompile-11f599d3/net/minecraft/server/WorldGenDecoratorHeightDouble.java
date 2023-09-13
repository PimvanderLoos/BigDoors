package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorHeightDouble extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorHeightDouble() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgendecoratorfrequencyconfiguration.a; ++i) {
            int j = random.nextInt(16);
            int k = random.nextInt(16);
            int l = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(j, 0, k)).getY() * 2;

            if (l > 0) {
                int i1 = random.nextInt(l);

                worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(j, i1, k), c0);
            }
        }

        return true;
    }
}

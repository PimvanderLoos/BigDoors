package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorHeight extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorHeight() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgendecoratorfrequencyconfiguration.a; ++i) {
            int j = random.nextInt(16);
            int k = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(j, 0, k)), c0);
        }

        return true;
    }
}

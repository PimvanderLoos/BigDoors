package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorForestRock extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorForestRock() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = random.nextInt(worldgendecoratorfrequencyconfiguration.a);

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(16);
            BlockPosition blockposition1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(k, 0, l));

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition1, c0);
        }

        return true;
    }
}

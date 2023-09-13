package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorHeight64 extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorHeight64() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgendecoratorfrequencyconfiguration.a; ++i) {
            int j = random.nextInt(16);
            boolean flag = true;
            int k = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(j, 64, k), c0);
        }

        return true;
    }
}

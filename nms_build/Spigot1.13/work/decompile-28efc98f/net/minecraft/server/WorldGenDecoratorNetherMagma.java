package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorNetherMagma extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorNetherMagma() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = generatoraccess.getSeaLevel() / 2 + 1;

        for (int j = 0; j < worldgendecoratorfrequencyconfiguration.a; ++j) {
            int k = random.nextInt(16);
            int l = i - 5 + random.nextInt(10);
            int i1 = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(k, l, i1), c0);
        }

        return true;
    }
}

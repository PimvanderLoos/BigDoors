package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorRoofedTree extends WorldGenDecorator<WorldGenFeatureDecoratorEmptyConfiguration> {

    public WorldGenDecoratorRoofedTree() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorEmptyConfiguration worldgenfeaturedecoratoremptyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int k = i * 4 + 1 + random.nextInt(3);
                int l = j * 4 + 1 + random.nextInt(3);

                worldgenerator.generate(generatoraccess, chunkgenerator, random, generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(k, 0, l)), c0);
            }
        }

        return true;
    }
}

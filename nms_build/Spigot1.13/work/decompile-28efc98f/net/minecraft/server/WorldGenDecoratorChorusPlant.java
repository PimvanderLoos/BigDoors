package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorChorusPlant extends WorldGenDecorator<WorldGenFeatureDecoratorEmptyConfiguration> {

    public WorldGenDecoratorChorusPlant() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorEmptyConfiguration worldgenfeaturedecoratoremptyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        boolean flag = false;
        int i = random.nextInt(5);

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(16);
            int i1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(k, 0, l)).getY();

            if (i1 > 0) {
                int j1 = i1 - 1;

                flag |= worldgenerator.generate(generatoraccess, chunkgenerator, random, new BlockPosition(blockposition.getX() + k, j1, blockposition.getZ() + l), c0);
            }
        }

        return flag;
    }
}

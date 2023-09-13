package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorNoiseHeight32 extends WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> {

    public WorldGenDecoratorNoiseHeight32() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorNoiseConfiguration worldgenfeaturedecoratornoiseconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        double d0 = BiomeBase.aJ.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D);
        int i = d0 < worldgenfeaturedecoratornoiseconfiguration.a ? worldgenfeaturedecoratornoiseconfiguration.b : worldgenfeaturedecoratornoiseconfiguration.c;

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(16);
            int i1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(k, 0, l)).getY() + 32;

            if (i1 > 0) {
                int j1 = random.nextInt(i1);

                worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(k, j1, l), c0);
            }
        }

        return true;
    }
}

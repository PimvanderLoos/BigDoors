package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorSolidTopNoise extends WorldGenDecorator<WorldGenDecoratorNoiseConfiguration> {

    public WorldGenDecoratorSolidTopNoise() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorNoiseConfiguration worldgendecoratornoiseconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        double d0 = BiomeBase.aJ.a((double) blockposition.getX() / worldgendecoratornoiseconfiguration.b, (double) blockposition.getZ() / worldgendecoratornoiseconfiguration.b);
        int i = (int) Math.ceil(d0 * (double) worldgendecoratornoiseconfiguration.a);

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(16);
            int i1 = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, blockposition.getX() + k, blockposition.getZ() + l);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, new BlockPosition(blockposition.getX() + k, i1, blockposition.getZ() + l), c0);
        }

        return false;
    }
}

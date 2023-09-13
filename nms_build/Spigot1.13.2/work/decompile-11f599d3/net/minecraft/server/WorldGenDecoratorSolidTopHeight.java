package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorSolidTopHeight extends WorldGenDecorator<WorldGenDecoratorRangeConfiguration> {

    public WorldGenDecoratorSolidTopHeight() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorRangeConfiguration worldgendecoratorrangeconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = random.nextInt(worldgendecoratorrangeconfiguration.b - worldgendecoratorrangeconfiguration.a) + worldgendecoratorrangeconfiguration.a;

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(16);
            int i1 = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, blockposition.getX() + k, blockposition.getZ() + l);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, new BlockPosition(blockposition.getX() + k, i1, blockposition.getZ() + l), c0);
        }

        return false;
    }
}

package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorSolidTop extends WorldGenDecorator<WorldGenFeatureDecoratorEmptyConfiguration> {

    public WorldGenDecoratorSolidTop() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorEmptyConfiguration worldgenfeaturedecoratoremptyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = random.nextInt(16);
        int j = random.nextInt(16);
        int k = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, blockposition.getX() + i, blockposition.getZ() + j);

        worldgenerator.generate(generatoraccess, chunkgenerator, random, new BlockPosition(blockposition.getX() + i, k, blockposition.getZ() + j), c0);
        return false;
    }
}

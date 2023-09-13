package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorSkyVisible extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorSkyVisible() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgendecoratorfrequencyconfiguration.a; ++i) {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();

            worldgenerator.generate(generatoraccess, chunkgenerator, random, new BlockPosition(j, generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, j, k), k), c0);
        }

        return true;
    }
}

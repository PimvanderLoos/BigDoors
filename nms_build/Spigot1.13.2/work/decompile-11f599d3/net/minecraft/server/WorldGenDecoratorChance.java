package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorChance extends WorldGenDecorator<WorldGenDecoratorChanceConfiguration> {

    public WorldGenDecoratorChance() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorChanceConfiguration worldgendecoratorchanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        if (random.nextFloat() < 1.0F / (float) worldgendecoratorchanceconfiguration.a) {
            int i = random.nextInt(16);
            int j = random.nextInt(16);
            BlockPosition blockposition1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(i, 0, j));

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition1, c0);
        }

        return true;
    }
}

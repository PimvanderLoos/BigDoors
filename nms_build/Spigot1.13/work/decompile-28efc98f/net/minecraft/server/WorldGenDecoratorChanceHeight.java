package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorChanceHeight extends WorldGenDecorator<WorldGenDecoratorChanceConfiguration> {

    public WorldGenDecoratorChanceHeight() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorChanceConfiguration worldgendecoratorchanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        if (random.nextFloat() < 1.0F / (float) worldgendecoratorchanceconfiguration.a) {
            int i = random.nextInt(16);
            int j = random.nextInt(16);
            int k = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(i, 0, j)).getY() * 2;

            if (k <= 0) {
                return false;
            }

            int l = random.nextInt(k);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(i, l, j), c0);
        }

        return true;
    }
}

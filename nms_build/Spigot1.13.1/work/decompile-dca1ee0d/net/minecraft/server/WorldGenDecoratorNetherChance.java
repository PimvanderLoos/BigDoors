package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorNetherChance extends WorldGenDecorator<WorldGenFeatureChanceDecoratorRangeConfiguration> {

    public WorldGenDecoratorNetherChance() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChanceDecoratorRangeConfiguration worldgenfeaturechancedecoratorrangeconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        if (random.nextFloat() < worldgenfeaturechancedecoratorrangeconfiguration.a) {
            int i = random.nextInt(16);
            int j = random.nextInt(worldgenfeaturechancedecoratorrangeconfiguration.d - worldgenfeaturechancedecoratorrangeconfiguration.b) + worldgenfeaturechancedecoratorrangeconfiguration.c;
            int k = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(i, j, k), c0);
        }

        return true;
    }
}

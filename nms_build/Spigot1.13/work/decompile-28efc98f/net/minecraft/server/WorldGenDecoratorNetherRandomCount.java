package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorNetherRandomCount extends WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> {

    public WorldGenDecoratorNetherRandomCount() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChanceDecoratorCountConfiguration worldgenfeaturechancedecoratorcountconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = random.nextInt(Math.max(worldgenfeaturechancedecoratorcountconfiguration.a, 1));

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(worldgenfeaturechancedecoratorcountconfiguration.d - worldgenfeaturechancedecoratorcountconfiguration.c) + worldgenfeaturechancedecoratorcountconfiguration.b;
            int i1 = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(k, l, i1), c0);
        }

        return true;
    }
}

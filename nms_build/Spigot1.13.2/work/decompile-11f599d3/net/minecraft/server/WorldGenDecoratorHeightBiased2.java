package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorHeightBiased2 extends WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> {

    public WorldGenDecoratorHeightBiased2() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChanceDecoratorCountConfiguration worldgenfeaturechancedecoratorcountconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgenfeaturechancedecoratorcountconfiguration.a; ++i) {
            int j = random.nextInt(16);
            int k = random.nextInt(16);
            int l = random.nextInt(random.nextInt(random.nextInt(worldgenfeaturechancedecoratorcountconfiguration.d - worldgenfeaturechancedecoratorcountconfiguration.c) + worldgenfeaturechancedecoratorcountconfiguration.b) + worldgenfeaturechancedecoratorcountconfiguration.b);
            BlockPosition blockposition1 = blockposition.a(j, l, k);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition1, c0);
        }

        return true;
    }
}

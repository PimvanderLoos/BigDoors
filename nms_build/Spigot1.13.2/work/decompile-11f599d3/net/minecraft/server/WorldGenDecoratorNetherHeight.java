package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorNetherHeight extends WorldGenDecorator<WorldGenFeatureChanceDecoratorCountConfiguration> {

    public WorldGenDecoratorNetherHeight() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChanceDecoratorCountConfiguration worldgenfeaturechancedecoratorcountconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        for (int i = 0; i < worldgenfeaturechancedecoratorcountconfiguration.a; ++i) {
            int j = random.nextInt(16);
            int k = random.nextInt(worldgenfeaturechancedecoratorcountconfiguration.d - worldgenfeaturechancedecoratorcountconfiguration.c) + worldgenfeaturechancedecoratorcountconfiguration.b;
            int l = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(j, k, l), c0);
        }

        return true;
    }
}

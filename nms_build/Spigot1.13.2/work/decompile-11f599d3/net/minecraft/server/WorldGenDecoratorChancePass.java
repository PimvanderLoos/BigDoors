package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorChancePass extends WorldGenDecorator<WorldGenDecoratorChanceConfiguration> {

    public WorldGenDecoratorChancePass() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorChanceConfiguration worldgendecoratorchanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        if (random.nextFloat() < 1.0F / (float) worldgendecoratorchanceconfiguration.a) {
            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition, c0);
        }

        return true;
    }
}

package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorEndIsland extends WorldGenDecorator<WorldGenFeatureDecoratorEmptyConfiguration> {

    public WorldGenDecoratorEndIsland() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorEmptyConfiguration worldgenfeaturedecoratoremptyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        boolean flag = false;

        if (random.nextInt(14) == 0) {
            flag |= worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16)), c0);
            if (random.nextInt(4) == 0) {
                flag |= worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(random.nextInt(16), 55 + random.nextInt(16), random.nextInt(16)), c0);
            }
        }

        return flag;
    }
}

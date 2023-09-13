package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorHeightExtraChance extends WorldGenDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> {

    public WorldGenDecoratorHeightExtraChance() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyExtraChanceConfiguration worldgendecoratorfrequencyextrachanceconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = worldgendecoratorfrequencyextrachanceconfiguration.a;

        if (random.nextFloat() < worldgendecoratorfrequencyextrachanceconfiguration.b) {
            i += worldgendecoratorfrequencyextrachanceconfiguration.c;
        }

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(k, 0, l)), c0);
        }

        return true;
    }
}

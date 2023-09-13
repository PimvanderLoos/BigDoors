package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorDungeon extends WorldGenDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorDungeon() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        int i = worldgendecoratordungeonconfiguration.a;

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(16);
            int l = random.nextInt(chunkgenerator.getGenerationDepth());
            int i1 = random.nextInt(16);

            worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition.a(k, l, i1), c0);
        }

        return true;
    }
}

package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureRandomChoice extends WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> {

    public WorldGenFeatureRandomChoice() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandomChoiceConfiguration worldgenfeaturerandomchoiceconfiguration) {
        for (int i = 0; i < worldgenfeaturerandomchoiceconfiguration.a.length; ++i) {
            if (random.nextFloat() < worldgenfeaturerandomchoiceconfiguration.c[i]) {
                return this.a(worldgenfeaturerandomchoiceconfiguration.a[i], worldgenfeaturerandomchoiceconfiguration.b[i], generatoraccess, chunkgenerator, random, blockposition);
            }
        }

        return this.a(worldgenfeaturerandomchoiceconfiguration.d, worldgenfeaturerandomchoiceconfiguration.f, generatoraccess, chunkgenerator, random, blockposition);
    }

    <FC extends WorldGenFeatureConfiguration> boolean a(WorldGenerator<FC> worldgenerator, WorldGenFeatureConfiguration worldgenfeatureconfiguration, GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition) {
        return worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition, worldgenfeatureconfiguration);
    }
}

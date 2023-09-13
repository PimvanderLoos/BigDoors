package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureChoice extends WorldGenerator<WorldGenFeatureChoiceConfiguration> {

    public WorldGenFeatureChoice() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration) {
        boolean flag = random.nextBoolean();

        return flag ? this.a(worldgenfeaturechoiceconfiguration.a, worldgenfeaturechoiceconfiguration.b, generatoraccess, chunkgenerator, random, blockposition) : this.a(worldgenfeaturechoiceconfiguration.c, worldgenfeaturechoiceconfiguration.d, generatoraccess, chunkgenerator, random, blockposition);
    }

    <FC extends WorldGenFeatureConfiguration> boolean a(WorldGenerator<FC> worldgenerator, WorldGenFeatureConfiguration worldgenfeatureconfiguration, GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition) {
        return worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition, worldgenfeatureconfiguration);
    }
}

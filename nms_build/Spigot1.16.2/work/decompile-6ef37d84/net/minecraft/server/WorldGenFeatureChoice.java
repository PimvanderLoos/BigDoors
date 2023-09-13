package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureChoice extends WorldGenerator<WorldGenFeatureChoiceConfiguration> {

    public WorldGenFeatureChoice(Codec<WorldGenFeatureChoiceConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration) {
        boolean flag = random.nextBoolean();

        return flag ? ((WorldGenFeatureConfigured) worldgenfeaturechoiceconfiguration.b.get()).a(generatoraccessseed, chunkgenerator, random, blockposition) : ((WorldGenFeatureConfigured) worldgenfeaturechoiceconfiguration.c.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}

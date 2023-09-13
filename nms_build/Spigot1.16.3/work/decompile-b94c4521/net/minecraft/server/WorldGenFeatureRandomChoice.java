package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;

public class WorldGenFeatureRandomChoice extends WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> {

    public WorldGenFeatureRandomChoice(Codec<WorldGenFeatureRandomChoiceConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandomChoiceConfiguration worldgenfeaturerandomchoiceconfiguration) {
        Iterator iterator = worldgenfeaturerandomchoiceconfiguration.b.iterator();

        WorldGenFeatureRandomChoiceConfigurationWeight worldgenfeaturerandomchoiceconfigurationweight;

        do {
            if (!iterator.hasNext()) {
                return ((WorldGenFeatureConfigured) worldgenfeaturerandomchoiceconfiguration.c.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
            }

            worldgenfeaturerandomchoiceconfigurationweight = (WorldGenFeatureRandomChoiceConfigurationWeight) iterator.next();
        } while (random.nextFloat() >= worldgenfeaturerandomchoiceconfigurationweight.c);

        return worldgenfeaturerandomchoiceconfigurationweight.a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomChoiceConfiguration;

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

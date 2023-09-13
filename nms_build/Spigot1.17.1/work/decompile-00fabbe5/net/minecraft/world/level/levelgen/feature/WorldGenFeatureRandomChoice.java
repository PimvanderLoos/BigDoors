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

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureRandomChoiceConfiguration> featureplacecontext) {
        WorldGenFeatureRandomChoiceConfiguration worldgenfeaturerandomchoiceconfiguration = (WorldGenFeatureRandomChoiceConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        ChunkGenerator chunkgenerator = featureplacecontext.b();
        BlockPosition blockposition = featureplacecontext.d();
        Iterator iterator = worldgenfeaturerandomchoiceconfiguration.features.iterator();

        WorldGenFeatureRandomChoiceConfigurationWeight worldgenfeaturerandomchoiceconfigurationweight;

        do {
            if (!iterator.hasNext()) {
                return ((WorldGenFeatureConfigured) worldgenfeaturerandomchoiceconfiguration.defaultFeature.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
            }

            worldgenfeaturerandomchoiceconfigurationweight = (WorldGenFeatureRandomChoiceConfigurationWeight) iterator.next();
        } while (random.nextFloat() >= worldgenfeaturerandomchoiceconfigurationweight.chance);

        return worldgenfeaturerandomchoiceconfigurationweight.a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}

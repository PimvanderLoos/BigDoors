package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;

public class WorldGenFeatureChoice extends WorldGenerator<WorldGenFeatureChoiceConfiguration> {

    public WorldGenFeatureChoice(Codec<WorldGenFeatureChoiceConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureChoiceConfiguration> featureplacecontext) {
        Random random = featureplacecontext.c();
        WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration = (WorldGenFeatureChoiceConfiguration) featureplacecontext.e();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        ChunkGenerator chunkgenerator = featureplacecontext.b();
        BlockPosition blockposition = featureplacecontext.d();
        boolean flag = random.nextBoolean();

        return flag ? ((WorldGenFeatureConfigured) worldgenfeaturechoiceconfiguration.featureTrue.get()).a(generatoraccessseed, chunkgenerator, random, blockposition) : ((WorldGenFeatureConfigured) worldgenfeaturechoiceconfiguration.featureFalse.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}

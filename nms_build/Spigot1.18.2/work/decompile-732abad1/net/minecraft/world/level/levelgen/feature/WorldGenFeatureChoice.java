package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureChoice extends WorldGenerator<WorldGenFeatureChoiceConfiguration> {

    public WorldGenFeatureChoice(Codec<WorldGenFeatureChoiceConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureChoiceConfiguration> featureplacecontext) {
        Random random = featureplacecontext.random();
        WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration = (WorldGenFeatureChoiceConfiguration) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkGenerator chunkgenerator = featureplacecontext.chunkGenerator();
        BlockPosition blockposition = featureplacecontext.origin();
        boolean flag = random.nextBoolean();

        return ((PlacedFeature) (flag ? worldgenfeaturechoiceconfiguration.featureTrue : worldgenfeaturechoiceconfiguration.featureFalse).value()).place(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}

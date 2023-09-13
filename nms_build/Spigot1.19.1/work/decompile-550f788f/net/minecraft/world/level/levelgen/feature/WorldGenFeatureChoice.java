package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
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
        RandomSource randomsource = featureplacecontext.random();
        WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration = (WorldGenFeatureChoiceConfiguration) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkGenerator chunkgenerator = featureplacecontext.chunkGenerator();
        BlockPosition blockposition = featureplacecontext.origin();
        boolean flag = randomsource.nextBoolean();

        return ((PlacedFeature) (flag ? worldgenfeaturechoiceconfiguration.featureTrue : worldgenfeaturechoiceconfiguration.featureFalse).value()).place(generatoraccessseed, chunkgenerator, randomsource, blockposition);
    }
}

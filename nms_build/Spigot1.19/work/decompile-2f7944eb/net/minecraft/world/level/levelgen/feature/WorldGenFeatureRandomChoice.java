package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomChoiceConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureRandomChoice extends WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> {

    public WorldGenFeatureRandomChoice(Codec<WorldGenFeatureRandomChoiceConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureRandomChoiceConfiguration> featureplacecontext) {
        WorldGenFeatureRandomChoiceConfiguration worldgenfeaturerandomchoiceconfiguration = (WorldGenFeatureRandomChoiceConfiguration) featureplacecontext.config();
        RandomSource randomsource = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkGenerator chunkgenerator = featureplacecontext.chunkGenerator();
        BlockPosition blockposition = featureplacecontext.origin();
        Iterator iterator = worldgenfeaturerandomchoiceconfiguration.features.iterator();

        WeightedPlacedFeature weightedplacedfeature;

        do {
            if (!iterator.hasNext()) {
                return ((PlacedFeature) worldgenfeaturerandomchoiceconfiguration.defaultFeature.value()).place(generatoraccessseed, chunkgenerator, randomsource, blockposition);
            }

            weightedplacedfeature = (WeightedPlacedFeature) iterator.next();
        } while (randomsource.nextFloat() >= weightedplacedfeature.chance);

        return weightedplacedfeature.place(generatoraccessseed, chunkgenerator, randomsource, blockposition);
    }
}

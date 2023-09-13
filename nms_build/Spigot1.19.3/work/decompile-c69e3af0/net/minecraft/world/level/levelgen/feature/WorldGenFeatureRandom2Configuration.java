package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureRandom2Configuration extends WorldGenerator<WorldGenFeatureRandom2> {

    public WorldGenFeatureRandom2Configuration(Codec<WorldGenFeatureRandom2> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureRandom2> featureplacecontext) {
        RandomSource randomsource = featureplacecontext.random();
        WorldGenFeatureRandom2 worldgenfeaturerandom2 = (WorldGenFeatureRandom2) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        ChunkGenerator chunkgenerator = featureplacecontext.chunkGenerator();
        int i = randomsource.nextInt(worldgenfeaturerandom2.features.size());
        PlacedFeature placedfeature = (PlacedFeature) worldgenfeaturerandom2.features.get(i).value();

        return placedfeature.place(generatoraccessseed, chunkgenerator, randomsource, blockposition);
    }
}

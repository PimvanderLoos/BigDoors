package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureRandomPatch extends WorldGenerator<WorldGenFeatureRandomPatchConfiguration> {

    public WorldGenFeatureRandomPatch(Codec<WorldGenFeatureRandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureRandomPatchConfiguration> featureplacecontext) {
        WorldGenFeatureRandomPatchConfiguration worldgenfeaturerandompatchconfiguration = (WorldGenFeatureRandomPatchConfiguration) featureplacecontext.config();
        RandomSource randomsource = featureplacecontext.random();
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        int i = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int j = worldgenfeaturerandompatchconfiguration.xzSpread() + 1;
        int k = worldgenfeaturerandompatchconfiguration.ySpread() + 1;

        for (int l = 0; l < worldgenfeaturerandompatchconfiguration.tries(); ++l) {
            blockposition_mutableblockposition.setWithOffset(blockposition, randomsource.nextInt(j) - randomsource.nextInt(j), randomsource.nextInt(k) - randomsource.nextInt(k), randomsource.nextInt(j) - randomsource.nextInt(j));
            if (((PlacedFeature) worldgenfeaturerandompatchconfiguration.feature().value()).place(generatoraccessseed, featureplacecontext.chunkGenerator(), randomsource, blockposition_mutableblockposition)) {
                ++i;
            }
        }

        return i > 0;
    }
}

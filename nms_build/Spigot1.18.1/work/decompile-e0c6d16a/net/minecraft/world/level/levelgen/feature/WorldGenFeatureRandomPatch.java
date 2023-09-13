package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
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
        Random random = featureplacecontext.random();
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        int i = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int j = worldgenfeaturerandompatchconfiguration.xzSpread() + 1;
        int k = worldgenfeaturerandompatchconfiguration.ySpread() + 1;

        for (int l = 0; l < worldgenfeaturerandompatchconfiguration.tries(); ++l) {
            blockposition_mutableblockposition.setWithOffset(blockposition, random.nextInt(j) - random.nextInt(j), random.nextInt(k) - random.nextInt(k), random.nextInt(j) - random.nextInt(j));
            if (((PlacedFeature) worldgenfeaturerandompatchconfiguration.feature().get()).place(generatoraccessseed, featureplacecontext.chunkGenerator(), random, blockposition_mutableblockposition)) {
                ++i;
            }
        }

        return i > 0;
    }
}

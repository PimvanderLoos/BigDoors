package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;

public class WorldGenLiquids extends WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> {

    public WorldGenLiquids(Codec<WorldGenFeatureHellFlowingLavaConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureHellFlowingLavaConfiguration> featureplacecontext) {
        WorldGenFeatureHellFlowingLavaConfiguration worldgenfeaturehellflowinglavaconfiguration = (WorldGenFeatureHellFlowingLavaConfiguration) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();

        if (!generatoraccessseed.getBlockState(blockposition.above()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
            return false;
        } else if (worldgenfeaturehellflowinglavaconfiguration.requiresBlockBelow && !generatoraccessseed.getBlockState(blockposition.below()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition);

            if (!iblockdata.isAir() && !iblockdata.is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
                return false;
            } else {
                int i = 0;
                int j = 0;

                if (generatoraccessseed.getBlockState(blockposition.west()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
                    ++j;
                }

                if (generatoraccessseed.getBlockState(blockposition.east()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
                    ++j;
                }

                if (generatoraccessseed.getBlockState(blockposition.north()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
                    ++j;
                }

                if (generatoraccessseed.getBlockState(blockposition.south()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
                    ++j;
                }

                if (generatoraccessseed.getBlockState(blockposition.below()).is(worldgenfeaturehellflowinglavaconfiguration.validBlocks)) {
                    ++j;
                }

                int k = 0;

                if (generatoraccessseed.isEmptyBlock(blockposition.west())) {
                    ++k;
                }

                if (generatoraccessseed.isEmptyBlock(blockposition.east())) {
                    ++k;
                }

                if (generatoraccessseed.isEmptyBlock(blockposition.north())) {
                    ++k;
                }

                if (generatoraccessseed.isEmptyBlock(blockposition.south())) {
                    ++k;
                }

                if (generatoraccessseed.isEmptyBlock(blockposition.below())) {
                    ++k;
                }

                if (j == worldgenfeaturehellflowinglavaconfiguration.rockCount && k == worldgenfeaturehellflowinglavaconfiguration.holeCount) {
                    generatoraccessseed.setBlock(blockposition, worldgenfeaturehellflowinglavaconfiguration.state.createLegacyBlock(), 2);
                    generatoraccessseed.scheduleTick(blockposition, worldgenfeaturehellflowinglavaconfiguration.state.getType(), 0);
                    ++i;
                }

                return i > 0;
            }
        }
    }
}

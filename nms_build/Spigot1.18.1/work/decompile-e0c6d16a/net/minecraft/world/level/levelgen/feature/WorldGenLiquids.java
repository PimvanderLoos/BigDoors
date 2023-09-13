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

        if (!worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.above()).getBlock())) {
            return false;
        } else if (worldgenfeaturehellflowinglavaconfiguration.requiresBlockBelow && !worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.below()).getBlock())) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition);

            if (!iblockdata.isAir() && !worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(iblockdata.getBlock())) {
                return false;
            } else {
                int i = 0;
                int j = 0;

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.west()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.east()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.north()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.south()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getBlockState(blockposition.below()).getBlock())) {
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

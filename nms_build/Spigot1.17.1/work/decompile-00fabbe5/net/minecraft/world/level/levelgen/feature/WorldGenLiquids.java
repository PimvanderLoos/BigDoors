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
    public boolean generate(FeaturePlaceContext<WorldGenFeatureHellFlowingLavaConfiguration> featureplacecontext) {
        WorldGenFeatureHellFlowingLavaConfiguration worldgenfeaturehellflowinglavaconfiguration = (WorldGenFeatureHellFlowingLavaConfiguration) featureplacecontext.e();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();

        if (!worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.up()).getBlock())) {
            return false;
        } else if (worldgenfeaturehellflowinglavaconfiguration.requiresBlockBelow && !worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.down()).getBlock())) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getType(blockposition);

            if (!iblockdata.isAir() && !worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(iblockdata.getBlock())) {
                return false;
            } else {
                int i = 0;
                int j = 0;

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.west()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.east()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.north()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.south()).getBlock())) {
                    ++j;
                }

                if (worldgenfeaturehellflowinglavaconfiguration.validBlocks.contains(generatoraccessseed.getType(blockposition.down()).getBlock())) {
                    ++j;
                }

                int k = 0;

                if (generatoraccessseed.isEmpty(blockposition.west())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.east())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.north())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.south())) {
                    ++k;
                }

                if (generatoraccessseed.isEmpty(blockposition.down())) {
                    ++k;
                }

                if (j == worldgenfeaturehellflowinglavaconfiguration.rockCount && k == worldgenfeaturehellflowinglavaconfiguration.holeCount) {
                    generatoraccessseed.setTypeAndData(blockposition, worldgenfeaturehellflowinglavaconfiguration.state.getBlockData(), 2);
                    generatoraccessseed.getFluidTickList().a(blockposition, worldgenfeaturehellflowinglavaconfiguration.state.getType(), 0);
                    ++i;
                }

                return i > 0;
            }
        }
    }
}

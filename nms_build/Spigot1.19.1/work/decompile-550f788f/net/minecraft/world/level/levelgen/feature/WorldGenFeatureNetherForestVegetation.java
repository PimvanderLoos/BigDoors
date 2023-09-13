package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;

public class WorldGenFeatureNetherForestVegetation extends WorldGenerator<NetherForestVegetationConfig> {

    public WorldGenFeatureNetherForestVegetation(Codec<NetherForestVegetationConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NetherForestVegetationConfig> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition.below());
        NetherForestVegetationConfig netherforestvegetationconfig = (NetherForestVegetationConfig) featureplacecontext.config();
        RandomSource randomsource = featureplacecontext.random();

        if (!iblockdata.is(TagsBlock.NYLIUM)) {
            return false;
        } else {
            int i = blockposition.getY();

            if (i >= generatoraccessseed.getMinBuildHeight() + 1 && i + 1 < generatoraccessseed.getMaxBuildHeight()) {
                int j = 0;

                for (int k = 0; k < netherforestvegetationconfig.spreadWidth * netherforestvegetationconfig.spreadWidth; ++k) {
                    BlockPosition blockposition1 = blockposition.offset(randomsource.nextInt(netherforestvegetationconfig.spreadWidth) - randomsource.nextInt(netherforestvegetationconfig.spreadWidth), randomsource.nextInt(netherforestvegetationconfig.spreadHeight) - randomsource.nextInt(netherforestvegetationconfig.spreadHeight), randomsource.nextInt(netherforestvegetationconfig.spreadWidth) - randomsource.nextInt(netherforestvegetationconfig.spreadWidth));
                    IBlockData iblockdata1 = netherforestvegetationconfig.stateProvider.getState(randomsource, blockposition1);

                    if (generatoraccessseed.isEmptyBlock(blockposition1) && blockposition1.getY() > generatoraccessseed.getMinBuildHeight() && iblockdata1.canSurvive(generatoraccessseed, blockposition1)) {
                        generatoraccessseed.setBlock(blockposition1, iblockdata1, 2);
                        ++j;
                    }
                }

                return j > 0;
            } else {
                return false;
            }
        }
    }
}

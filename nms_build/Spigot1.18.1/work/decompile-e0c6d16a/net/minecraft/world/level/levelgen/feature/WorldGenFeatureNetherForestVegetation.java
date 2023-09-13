package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
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
        Random random = featureplacecontext.random();

        if (!iblockdata.is((Tag) TagsBlock.NYLIUM)) {
            return false;
        } else {
            int i = blockposition.getY();

            if (i >= generatoraccessseed.getMinBuildHeight() + 1 && i + 1 < generatoraccessseed.getMaxBuildHeight()) {
                int j = 0;

                for (int k = 0; k < netherforestvegetationconfig.spreadWidth * netherforestvegetationconfig.spreadWidth; ++k) {
                    BlockPosition blockposition1 = blockposition.offset(random.nextInt(netherforestvegetationconfig.spreadWidth) - random.nextInt(netherforestvegetationconfig.spreadWidth), random.nextInt(netherforestvegetationconfig.spreadHeight) - random.nextInt(netherforestvegetationconfig.spreadHeight), random.nextInt(netherforestvegetationconfig.spreadWidth) - random.nextInt(netherforestvegetationconfig.spreadWidth));
                    IBlockData iblockdata1 = netherforestvegetationconfig.stateProvider.getState(random, blockposition1);

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

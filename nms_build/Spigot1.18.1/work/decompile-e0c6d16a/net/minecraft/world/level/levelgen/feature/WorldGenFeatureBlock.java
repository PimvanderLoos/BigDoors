package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockTallPlant;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;

public class WorldGenFeatureBlock extends WorldGenerator<WorldGenFeatureBlockConfiguration> {

    public WorldGenFeatureBlock(Codec<WorldGenFeatureBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureBlockConfiguration> featureplacecontext) {
        WorldGenFeatureBlockConfiguration worldgenfeatureblockconfiguration = (WorldGenFeatureBlockConfiguration) featureplacecontext.config();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        IBlockData iblockdata = worldgenfeatureblockconfiguration.toPlace().getState(featureplacecontext.random(), blockposition);

        if (iblockdata.canSurvive(generatoraccessseed, blockposition)) {
            if (iblockdata.getBlock() instanceof BlockTallPlant) {
                if (!generatoraccessseed.isEmptyBlock(blockposition.above())) {
                    return false;
                }

                BlockTallPlant.placeAt(generatoraccessseed, iblockdata, blockposition, 2);
            } else {
                generatoraccessseed.setBlock(blockposition, iblockdata, 2);
            }

            return true;
        } else {
            return false;
        }
    }
}

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
    public boolean generate(FeaturePlaceContext<WorldGenFeatureBlockConfiguration> featureplacecontext) {
        WorldGenFeatureBlockConfiguration worldgenfeatureblockconfiguration = (WorldGenFeatureBlockConfiguration) featureplacecontext.e();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();

        if ((worldgenfeatureblockconfiguration.placeOn.isEmpty() || worldgenfeatureblockconfiguration.placeOn.contains(generatoraccessseed.getType(blockposition.down()))) && (worldgenfeatureblockconfiguration.placeIn.isEmpty() || worldgenfeatureblockconfiguration.placeIn.contains(generatoraccessseed.getType(blockposition))) && (worldgenfeatureblockconfiguration.placeUnder.isEmpty() || worldgenfeatureblockconfiguration.placeUnder.contains(generatoraccessseed.getType(blockposition.up())))) {
            IBlockData iblockdata = worldgenfeatureblockconfiguration.toPlace.a(featureplacecontext.c(), blockposition);

            if (iblockdata.canPlace(generatoraccessseed, blockposition)) {
                if (iblockdata.getBlock() instanceof BlockTallPlant) {
                    if (!generatoraccessseed.isEmpty(blockposition.up())) {
                        return false;
                    }

                    BlockTallPlant.a(generatoraccessseed, iblockdata, blockposition, 2);
                } else {
                    generatoraccessseed.setTypeAndData(blockposition, iblockdata, 2);
                }

                return true;
            }
        }

        return false;
    }
}

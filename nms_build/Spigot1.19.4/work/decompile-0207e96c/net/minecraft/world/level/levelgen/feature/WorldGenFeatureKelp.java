package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockKelp;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureKelp extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureKelp(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        int i = 0;
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();
        int j = generatoraccessseed.getHeight(HeightMap.Type.OCEAN_FLOOR, blockposition.getX(), blockposition.getZ());
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ());

        if (generatoraccessseed.getBlockState(blockposition1).is(Blocks.WATER)) {
            IBlockData iblockdata = Blocks.KELP.defaultBlockState();
            IBlockData iblockdata1 = Blocks.KELP_PLANT.defaultBlockState();
            int k = 1 + randomsource.nextInt(10);

            for (int l = 0; l <= k; ++l) {
                if (generatoraccessseed.getBlockState(blockposition1).is(Blocks.WATER) && generatoraccessseed.getBlockState(blockposition1.above()).is(Blocks.WATER) && iblockdata1.canSurvive(generatoraccessseed, blockposition1)) {
                    if (l == k) {
                        generatoraccessseed.setBlock(blockposition1, (IBlockData) iblockdata.setValue(BlockKelp.AGE, randomsource.nextInt(4) + 20), 2);
                        ++i;
                    } else {
                        generatoraccessseed.setBlock(blockposition1, iblockdata1, 2);
                    }
                } else if (l > 0) {
                    BlockPosition blockposition2 = blockposition1.below();

                    if (iblockdata.canSurvive(generatoraccessseed, blockposition2) && !generatoraccessseed.getBlockState(blockposition2.below()).is(Blocks.KELP)) {
                        generatoraccessseed.setBlock(blockposition2, (IBlockData) iblockdata.setValue(BlockKelp.AGE, randomsource.nextInt(4) + 20), 2);
                        ++i;
                    }
                    break;
                }

                blockposition1 = blockposition1.above();
            }
        }

        return i > 0;
    }
}

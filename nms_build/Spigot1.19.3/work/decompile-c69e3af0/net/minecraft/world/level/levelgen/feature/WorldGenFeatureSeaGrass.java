package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;

public class WorldGenFeatureSeaGrass extends WorldGenerator<WorldGenFeatureConfigurationChance> {

    public WorldGenFeatureSeaGrass(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureConfigurationChance> featureplacecontext) {
        boolean flag = false;
        RandomSource randomsource = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance = (WorldGenFeatureConfigurationChance) featureplacecontext.config();
        int i = randomsource.nextInt(8) - randomsource.nextInt(8);
        int j = randomsource.nextInt(8) - randomsource.nextInt(8);
        int k = generatoraccessseed.getHeight(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + i, blockposition.getZ() + j);
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + i, k, blockposition.getZ() + j);

        if (generatoraccessseed.getBlockState(blockposition1).is(Blocks.WATER)) {
            boolean flag1 = randomsource.nextDouble() < (double) worldgenfeatureconfigurationchance.probability;
            IBlockData iblockdata = flag1 ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();

            if (iblockdata.canSurvive(generatoraccessseed, blockposition1)) {
                if (flag1) {
                    IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(TallSeagrassBlock.HALF, BlockPropertyDoubleBlockHalf.UPPER);
                    BlockPosition blockposition2 = blockposition1.above();

                    if (generatoraccessseed.getBlockState(blockposition2).is(Blocks.WATER)) {
                        generatoraccessseed.setBlock(blockposition1, iblockdata, 2);
                        generatoraccessseed.setBlock(blockposition2, iblockdata1, 2);
                    }
                } else {
                    generatoraccessseed.setBlock(blockposition1, iblockdata, 2);
                }

                flag = true;
            }
        }

        return flag;
    }
}

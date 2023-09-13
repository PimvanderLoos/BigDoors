package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.BlockHugeMushroom;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;

public class WorldGenHugeMushroomBrown extends WorldGenMushrooms {

    public WorldGenHugeMushroomBrown(Codec<WorldGenFeatureMushroomConfiguration> codec) {
        super(codec);
    }

    @Override
    protected void makeCap(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration) {
        int j = worldgenfeaturemushroomconfiguration.foliageRadius;

        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean flag = k == -j;
                boolean flag1 = k == j;
                boolean flag2 = l == -j;
                boolean flag3 = l == j;
                boolean flag4 = flag || flag1;
                boolean flag5 = flag2 || flag3;

                if (!flag4 || !flag5) {
                    blockposition_mutableblockposition.setWithOffset(blockposition, k, i, l);
                    if (!generatoraccess.getBlockState(blockposition_mutableblockposition).isSolidRender(generatoraccess, blockposition_mutableblockposition)) {
                        boolean flag6 = flag || flag5 && k == 1 - j;
                        boolean flag7 = flag1 || flag5 && k == j - 1;
                        boolean flag8 = flag2 || flag4 && l == 1 - j;
                        boolean flag9 = flag3 || flag4 && l == j - 1;
                        IBlockData iblockdata = worldgenfeaturemushroomconfiguration.capProvider.getState(randomsource, blockposition);

                        if (iblockdata.hasProperty(BlockHugeMushroom.WEST) && iblockdata.hasProperty(BlockHugeMushroom.EAST) && iblockdata.hasProperty(BlockHugeMushroom.NORTH) && iblockdata.hasProperty(BlockHugeMushroom.SOUTH)) {
                            iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockHugeMushroom.WEST, flag6)).setValue(BlockHugeMushroom.EAST, flag7)).setValue(BlockHugeMushroom.NORTH, flag8)).setValue(BlockHugeMushroom.SOUTH, flag9);
                        }

                        this.setBlock(generatoraccess, blockposition_mutableblockposition, iblockdata);
                    }
                }
            }
        }

    }

    @Override
    protected int getTreeRadiusForHeight(int i, int j, int k, int l) {
        return l <= 3 ? 0 : k;
    }
}

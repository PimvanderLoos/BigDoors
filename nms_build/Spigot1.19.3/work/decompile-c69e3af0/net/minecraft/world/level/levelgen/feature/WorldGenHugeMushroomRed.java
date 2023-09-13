package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.BlockHugeMushroom;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;

public class WorldGenHugeMushroomRed extends WorldGenMushrooms {

    public WorldGenHugeMushroomRed(Codec<WorldGenFeatureMushroomConfiguration> codec) {
        super(codec);
    }

    @Override
    protected void makeCap(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration) {
        for (int j = i - 3; j <= i; ++j) {
            int k = j < i ? worldgenfeaturemushroomconfiguration.foliageRadius : worldgenfeaturemushroomconfiguration.foliageRadius - 1;
            int l = worldgenfeaturemushroomconfiguration.foliageRadius - 2;

            for (int i1 = -k; i1 <= k; ++i1) {
                for (int j1 = -k; j1 <= k; ++j1) {
                    boolean flag = i1 == -k;
                    boolean flag1 = i1 == k;
                    boolean flag2 = j1 == -k;
                    boolean flag3 = j1 == k;
                    boolean flag4 = flag || flag1;
                    boolean flag5 = flag2 || flag3;

                    if (j >= i || flag4 != flag5) {
                        blockposition_mutableblockposition.setWithOffset(blockposition, i1, j, j1);
                        if (!generatoraccess.getBlockState(blockposition_mutableblockposition).isSolidRender(generatoraccess, blockposition_mutableblockposition)) {
                            IBlockData iblockdata = worldgenfeaturemushroomconfiguration.capProvider.getState(randomsource, blockposition);

                            if (iblockdata.hasProperty(BlockHugeMushroom.WEST) && iblockdata.hasProperty(BlockHugeMushroom.EAST) && iblockdata.hasProperty(BlockHugeMushroom.NORTH) && iblockdata.hasProperty(BlockHugeMushroom.SOUTH) && iblockdata.hasProperty(BlockHugeMushroom.UP)) {
                                iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockHugeMushroom.UP, j >= i - 1)).setValue(BlockHugeMushroom.WEST, i1 < -l)).setValue(BlockHugeMushroom.EAST, i1 > l)).setValue(BlockHugeMushroom.NORTH, j1 < -l)).setValue(BlockHugeMushroom.SOUTH, j1 > l);
                            }

                            this.setBlock(generatoraccess, blockposition_mutableblockposition, iblockdata);
                        }
                    }
                }
            }
        }

    }

    @Override
    protected int getTreeRadiusForHeight(int i, int j, int k, int l) {
        int i1 = 0;

        if (l < j && l >= j - 3) {
            i1 = k;
        } else if (l == j) {
            i1 = k;
        }

        return i1;
    }
}

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockGrowingTop;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;

public class WorldGenFeatureTwistingVines extends WorldGenerator<TwistingVinesConfig> {

    public WorldGenFeatureTwistingVines(Codec<TwistingVinesConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TwistingVinesConfig> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();

        if (isInvalidPlacementLocation(generatoraccessseed, blockposition)) {
            return false;
        } else {
            RandomSource randomsource = featureplacecontext.random();
            TwistingVinesConfig twistingvinesconfig = (TwistingVinesConfig) featureplacecontext.config();
            int i = twistingvinesconfig.spreadWidth();
            int j = twistingvinesconfig.spreadHeight();
            int k = twistingvinesconfig.maxHeight();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int l = 0; l < i * i; ++l) {
                blockposition_mutableblockposition.set(blockposition).move(MathHelper.nextInt(randomsource, -i, i), MathHelper.nextInt(randomsource, -j, j), MathHelper.nextInt(randomsource, -i, i));
                if (findFirstAirBlockAboveGround(generatoraccessseed, blockposition_mutableblockposition) && !isInvalidPlacementLocation(generatoraccessseed, blockposition_mutableblockposition)) {
                    int i1 = MathHelper.nextInt(randomsource, 1, k);

                    if (randomsource.nextInt(6) == 0) {
                        i1 *= 2;
                    }

                    if (randomsource.nextInt(5) == 0) {
                        i1 = 1;
                    }

                    boolean flag = true;
                    boolean flag1 = true;

                    placeWeepingVinesColumn(generatoraccessseed, randomsource, blockposition_mutableblockposition, i1, 17, 25);
                }
            }

            return true;
        }
    }

    private static boolean findFirstAirBlockAboveGround(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        do {
            blockposition_mutableblockposition.move(0, -1, 0);
            if (generatoraccess.isOutsideBuildHeight(blockposition_mutableblockposition)) {
                return false;
            }
        } while (generatoraccess.getBlockState(blockposition_mutableblockposition).isAir());

        blockposition_mutableblockposition.move(0, 1, 0);
        return true;
    }

    public static void placeWeepingVinesColumn(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i, int j, int k) {
        for (int l = 1; l <= i; ++l) {
            if (generatoraccess.isEmptyBlock(blockposition_mutableblockposition)) {
                if (l == i || !generatoraccess.isEmptyBlock(blockposition_mutableblockposition.above())) {
                    generatoraccess.setBlock(blockposition_mutableblockposition, (IBlockData) Blocks.TWISTING_VINES.defaultBlockState().setValue(BlockGrowingTop.AGE, MathHelper.nextInt(randomsource, j, k)), 2);
                    break;
                }

                generatoraccess.setBlock(blockposition_mutableblockposition, Blocks.TWISTING_VINES_PLANT.defaultBlockState(), 2);
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

    }

    private static boolean isInvalidPlacementLocation(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!generatoraccess.isEmptyBlock(blockposition)) {
            return true;
        } else {
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition.below());

            return !iblockdata.is(Blocks.NETHERRACK) && !iblockdata.is(Blocks.WARPED_NYLIUM) && !iblockdata.is(Blocks.WARPED_WART_BLOCK);
        }
    }
}

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
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureWeepingVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();

    public WorldGenFeatureWeepingVines(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();

        if (!generatoraccessseed.isEmptyBlock(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition.above());

            if (!iblockdata.is(Blocks.NETHERRACK) && !iblockdata.is(Blocks.NETHER_WART_BLOCK)) {
                return false;
            } else {
                this.placeRoofNetherWart(generatoraccessseed, randomsource, blockposition);
                this.placeRoofWeepingVines(generatoraccessseed, randomsource, blockposition);
                return true;
            }
        }
    }

    private void placeRoofNetherWart(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition) {
        generatoraccess.setBlock(blockposition, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 200; ++i) {
            blockposition_mutableblockposition.setWithOffset(blockposition, randomsource.nextInt(6) - randomsource.nextInt(6), randomsource.nextInt(2) - randomsource.nextInt(5), randomsource.nextInt(6) - randomsource.nextInt(6));
            if (generatoraccess.isEmptyBlock(blockposition_mutableblockposition)) {
                int j = 0;
                EnumDirection[] aenumdirection = WorldGenFeatureWeepingVines.DIRECTIONS;
                int k = aenumdirection.length;

                for (int l = 0; l < k; ++l) {
                    EnumDirection enumdirection = aenumdirection[l];
                    IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, enumdirection));

                    if (iblockdata.is(Blocks.NETHERRACK) || iblockdata.is(Blocks.NETHER_WART_BLOCK)) {
                        ++j;
                    }

                    if (j > 1) {
                        break;
                    }
                }

                if (j == 1) {
                    generatoraccess.setBlock(blockposition_mutableblockposition, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
                }
            }
        }

    }

    private void placeRoofWeepingVines(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i = 0; i < 100; ++i) {
            blockposition_mutableblockposition.setWithOffset(blockposition, randomsource.nextInt(8) - randomsource.nextInt(8), randomsource.nextInt(2) - randomsource.nextInt(7), randomsource.nextInt(8) - randomsource.nextInt(8));
            if (generatoraccess.isEmptyBlock(blockposition_mutableblockposition)) {
                IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition.above());

                if (iblockdata.is(Blocks.NETHERRACK) || iblockdata.is(Blocks.NETHER_WART_BLOCK)) {
                    int j = MathHelper.nextInt(randomsource, 1, 8);

                    if (randomsource.nextInt(6) == 0) {
                        j *= 2;
                    }

                    if (randomsource.nextInt(5) == 0) {
                        j = 1;
                    }

                    boolean flag = true;
                    boolean flag1 = true;

                    placeWeepingVinesColumn(generatoraccess, randomsource, blockposition_mutableblockposition, j, 17, 25);
                }
            }
        }

    }

    public static void placeWeepingVinesColumn(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i, int j, int k) {
        for (int l = 0; l <= i; ++l) {
            if (generatoraccess.isEmptyBlock(blockposition_mutableblockposition)) {
                if (l == i || !generatoraccess.isEmptyBlock(blockposition_mutableblockposition.below())) {
                    generatoraccess.setBlock(blockposition_mutableblockposition, (IBlockData) Blocks.WEEPING_VINES.defaultBlockState().setValue(BlockGrowingTop.AGE, MathHelper.nextInt(randomsource, j, k)), 2);
                    break;
                }

                generatoraccess.setBlock(blockposition_mutableblockposition, Blocks.WEEPING_VINES_PLANT.defaultBlockState(), 2);
            }

            blockposition_mutableblockposition.move(EnumDirection.DOWN);
        }

    }
}

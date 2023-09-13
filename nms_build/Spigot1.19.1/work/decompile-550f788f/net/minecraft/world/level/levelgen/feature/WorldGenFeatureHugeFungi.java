package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Material;

public class WorldGenFeatureHugeFungi extends WorldGenerator<WorldGenFeatureHugeFungiConfiguration> {

    private static final float HUGE_PROBABILITY = 0.06F;

    public WorldGenFeatureHugeFungi(Codec<WorldGenFeatureHugeFungiConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureHugeFungiConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();
        ChunkGenerator chunkgenerator = featureplacecontext.chunkGenerator();
        WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration = (WorldGenFeatureHugeFungiConfiguration) featureplacecontext.config();
        Block block = worldgenfeaturehugefungiconfiguration.validBaseState.getBlock();
        BlockPosition blockposition1 = null;
        IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition.below());

        if (iblockdata.is(block)) {
            blockposition1 = blockposition;
        }

        if (blockposition1 == null) {
            return false;
        } else {
            int i = MathHelper.nextInt(randomsource, 4, 13);

            if (randomsource.nextInt(12) == 0) {
                i *= 2;
            }

            if (!worldgenfeaturehugefungiconfiguration.planted) {
                int j = chunkgenerator.getGenDepth();

                if (blockposition1.getY() + i + 1 >= j) {
                    return false;
                }
            }

            boolean flag = !worldgenfeaturehugefungiconfiguration.planted && randomsource.nextFloat() < 0.06F;

            generatoraccessseed.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 4);
            this.placeStem(generatoraccessseed, randomsource, worldgenfeaturehugefungiconfiguration, blockposition1, i, flag);
            this.placeHat(generatoraccessseed, randomsource, worldgenfeaturehugefungiconfiguration, blockposition1, i, flag);
            return true;
        }
    }

    private static boolean isReplaceable(GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        return generatoraccess.isStateAtPosition(blockposition, (iblockdata) -> {
            Material material = iblockdata.getMaterial();

            return iblockdata.getMaterial().isReplaceable() || flag && material == Material.PLANT;
        });
    }

    private void placeStem(GeneratorAccess generatoraccess, RandomSource randomsource, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration, BlockPosition blockposition, int i, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        IBlockData iblockdata = worldgenfeaturehugefungiconfiguration.stemState;
        int j = flag ? 1 : 0;

        for (int k = -j; k <= j; ++k) {
            for (int l = -j; l <= j; ++l) {
                boolean flag1 = flag && MathHelper.abs(k) == j && MathHelper.abs(l) == j;

                for (int i1 = 0; i1 < i; ++i1) {
                    blockposition_mutableblockposition.setWithOffset(blockposition, k, i1, l);
                    if (isReplaceable(generatoraccess, blockposition_mutableblockposition, true)) {
                        if (worldgenfeaturehugefungiconfiguration.planted) {
                            if (!generatoraccess.getBlockState(blockposition_mutableblockposition.below()).isAir()) {
                                generatoraccess.destroyBlock(blockposition_mutableblockposition, true);
                            }

                            generatoraccess.setBlock(blockposition_mutableblockposition, iblockdata, 3);
                        } else if (flag1) {
                            if (randomsource.nextFloat() < 0.1F) {
                                this.setBlock(generatoraccess, blockposition_mutableblockposition, iblockdata);
                            }
                        } else {
                            this.setBlock(generatoraccess, blockposition_mutableblockposition, iblockdata);
                        }
                    }
                }
            }
        }

    }

    private void placeHat(GeneratorAccess generatoraccess, RandomSource randomsource, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration, BlockPosition blockposition, int i, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        boolean flag1 = worldgenfeaturehugefungiconfiguration.hatState.is(Blocks.NETHER_WART_BLOCK);
        int j = Math.min(randomsource.nextInt(1 + i / 3) + 5, i);
        int k = i - j;

        for (int l = k; l <= i; ++l) {
            int i1 = l < i - randomsource.nextInt(3) ? 2 : 1;

            if (j > 8 && l < k + 4) {
                i1 = 3;
            }

            if (flag) {
                ++i1;
            }

            for (int j1 = -i1; j1 <= i1; ++j1) {
                for (int k1 = -i1; k1 <= i1; ++k1) {
                    boolean flag2 = j1 == -i1 || j1 == i1;
                    boolean flag3 = k1 == -i1 || k1 == i1;
                    boolean flag4 = !flag2 && !flag3 && l != i;
                    boolean flag5 = flag2 && flag3;
                    boolean flag6 = l < k + 3;

                    blockposition_mutableblockposition.setWithOffset(blockposition, j1, l, k1);
                    if (isReplaceable(generatoraccess, blockposition_mutableblockposition, false)) {
                        if (worldgenfeaturehugefungiconfiguration.planted && !generatoraccess.getBlockState(blockposition_mutableblockposition.below()).isAir()) {
                            generatoraccess.destroyBlock(blockposition_mutableblockposition, true);
                        }

                        if (flag6) {
                            if (!flag4) {
                                this.placeHatDropBlock(generatoraccess, randomsource, blockposition_mutableblockposition, worldgenfeaturehugefungiconfiguration.hatState, flag1);
                            }
                        } else if (flag4) {
                            this.placeHatBlock(generatoraccess, randomsource, worldgenfeaturehugefungiconfiguration, blockposition_mutableblockposition, 0.1F, 0.2F, flag1 ? 0.1F : 0.0F);
                        } else if (flag5) {
                            this.placeHatBlock(generatoraccess, randomsource, worldgenfeaturehugefungiconfiguration, blockposition_mutableblockposition, 0.01F, 0.7F, flag1 ? 0.083F : 0.0F);
                        } else {
                            this.placeHatBlock(generatoraccess, randomsource, worldgenfeaturehugefungiconfiguration, blockposition_mutableblockposition, 5.0E-4F, 0.98F, flag1 ? 0.07F : 0.0F);
                        }
                    }
                }
            }
        }

    }

    private void placeHatBlock(GeneratorAccess generatoraccess, RandomSource randomsource, WorldGenFeatureHugeFungiConfiguration worldgenfeaturehugefungiconfiguration, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, float f, float f1, float f2) {
        if (randomsource.nextFloat() < f) {
            this.setBlock(generatoraccess, blockposition_mutableblockposition, worldgenfeaturehugefungiconfiguration.decorState);
        } else if (randomsource.nextFloat() < f1) {
            this.setBlock(generatoraccess, blockposition_mutableblockposition, worldgenfeaturehugefungiconfiguration.hatState);
            if (randomsource.nextFloat() < f2) {
                tryPlaceWeepingVines(blockposition_mutableblockposition, generatoraccess, randomsource);
            }
        }

    }

    private void placeHatDropBlock(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        if (generatoraccess.getBlockState(blockposition.below()).is(iblockdata.getBlock())) {
            this.setBlock(generatoraccess, blockposition, iblockdata);
        } else if ((double) randomsource.nextFloat() < 0.15D) {
            this.setBlock(generatoraccess, blockposition, iblockdata);
            if (flag && randomsource.nextInt(11) == 0) {
                tryPlaceWeepingVines(blockposition, generatoraccess, randomsource);
            }
        }

    }

    private static void tryPlaceWeepingVines(BlockPosition blockposition, GeneratorAccess generatoraccess, RandomSource randomsource) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable().move(EnumDirection.DOWN);

        if (generatoraccess.isEmptyBlock(blockposition_mutableblockposition)) {
            int i = MathHelper.nextInt(randomsource, 1, 5);

            if (randomsource.nextInt(7) == 0) {
                i *= 2;
            }

            boolean flag = true;
            boolean flag1 = true;

            WorldGenFeatureWeepingVines.placeWeepingVinesColumn(generatoraccess, randomsource, blockposition_mutableblockposition, i, 23, 25);
        }
    }
}

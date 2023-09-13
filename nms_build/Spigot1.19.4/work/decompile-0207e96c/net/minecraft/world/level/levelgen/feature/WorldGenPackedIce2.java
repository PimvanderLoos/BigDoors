package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenPackedIce2 extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenPackedIce2(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();

        GeneratorAccessSeed generatoraccessseed;

        for (generatoraccessseed = featureplacecontext.level(); generatoraccessseed.isEmptyBlock(blockposition) && blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 2; blockposition = blockposition.below()) {
            ;
        }

        if (!generatoraccessseed.getBlockState(blockposition).is(Blocks.SNOW_BLOCK)) {
            return false;
        } else {
            blockposition = blockposition.above(randomsource.nextInt(4));
            int i = randomsource.nextInt(4) + 7;
            int j = i / 4 + randomsource.nextInt(2);

            if (j > 1 && randomsource.nextInt(60) == 0) {
                blockposition = blockposition.above(10 + randomsource.nextInt(30));
            }

            int k;
            int l;

            for (k = 0; k < i; ++k) {
                float f = (1.0F - (float) k / (float) i) * (float) j;

                l = MathHelper.ceil(f);

                for (int i1 = -l; i1 <= l; ++i1) {
                    float f1 = (float) MathHelper.abs(i1) - 0.25F;

                    for (int j1 = -l; j1 <= l; ++j1) {
                        float f2 = (float) MathHelper.abs(j1) - 0.25F;

                        if ((i1 == 0 && j1 == 0 || f1 * f1 + f2 * f2 <= f * f) && (i1 != -l && i1 != l && j1 != -l && j1 != l || randomsource.nextFloat() <= 0.75F)) {
                            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition.offset(i1, k, j1));

                            if (iblockdata.isAir() || isDirt(iblockdata) || iblockdata.is(Blocks.SNOW_BLOCK) || iblockdata.is(Blocks.ICE)) {
                                this.setBlock(generatoraccessseed, blockposition.offset(i1, k, j1), Blocks.PACKED_ICE.defaultBlockState());
                            }

                            if (k != 0 && l > 1) {
                                iblockdata = generatoraccessseed.getBlockState(blockposition.offset(i1, -k, j1));
                                if (iblockdata.isAir() || isDirt(iblockdata) || iblockdata.is(Blocks.SNOW_BLOCK) || iblockdata.is(Blocks.ICE)) {
                                    this.setBlock(generatoraccessseed, blockposition.offset(i1, -k, j1), Blocks.PACKED_ICE.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }

            k = j - 1;
            if (k < 0) {
                k = 0;
            } else if (k > 1) {
                k = 1;
            }

            for (int k1 = -k; k1 <= k; ++k1) {
                l = -k;

                while (l <= k) {
                    BlockPosition blockposition1 = blockposition.offset(k1, -1, l);
                    int l1 = 50;

                    if (Math.abs(k1) == 1 && Math.abs(l) == 1) {
                        l1 = randomsource.nextInt(5);
                    }

                    while (true) {
                        if (blockposition1.getY() > 50) {
                            IBlockData iblockdata1 = generatoraccessseed.getBlockState(blockposition1);

                            if (iblockdata1.isAir() || isDirt(iblockdata1) || iblockdata1.is(Blocks.SNOW_BLOCK) || iblockdata1.is(Blocks.ICE) || iblockdata1.is(Blocks.PACKED_ICE)) {
                                this.setBlock(generatoraccessseed, blockposition1, Blocks.PACKED_ICE.defaultBlockState());
                                blockposition1 = blockposition1.below();
                                --l1;
                                if (l1 <= 0) {
                                    blockposition1 = blockposition1.below(randomsource.nextInt(5) + 1);
                                    l1 = randomsource.nextInt(5);
                                }
                                continue;
                            }
                        }

                        ++l;
                        break;
                    }
                }
            }

            return true;
        }
    }
}

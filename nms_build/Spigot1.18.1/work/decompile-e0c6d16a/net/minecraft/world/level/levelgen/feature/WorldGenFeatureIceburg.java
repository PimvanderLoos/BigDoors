package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;
import net.minecraft.world.level.material.Material;

public class WorldGenFeatureIceburg extends WorldGenerator<WorldGenFeatureLakeConfiguration> {

    public WorldGenFeatureIceburg(Codec<WorldGenFeatureLakeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureLakeConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();

        blockposition = new BlockPosition(blockposition.getX(), featureplacecontext.chunkGenerator().getSeaLevel(), blockposition.getZ());
        Random random = featureplacecontext.random();
        boolean flag = random.nextDouble() > 0.7D;
        IBlockData iblockdata = ((WorldGenFeatureLakeConfiguration) featureplacecontext.config()).state;
        double d0 = random.nextDouble() * 2.0D * 3.141592653589793D;
        int i = 11 - random.nextInt(5);
        int j = 3 + random.nextInt(3);
        boolean flag1 = random.nextDouble() > 0.7D;
        boolean flag2 = true;
        int k = flag1 ? random.nextInt(6) + 6 : random.nextInt(15) + 3;

        if (!flag1 && random.nextDouble() > 0.9D) {
            k += random.nextInt(19) + 7;
        }

        int l = Math.min(k + random.nextInt(11), 18);
        int i1 = Math.min(k + random.nextInt(7) - random.nextInt(5), 11);
        int j1 = flag1 ? i : 11;

        int k1;
        int l1;
        int i2;
        int j2;

        for (i2 = -j1; i2 < j1; ++i2) {
            for (j2 = -j1; j2 < j1; ++j2) {
                for (l1 = 0; l1 < k; ++l1) {
                    k1 = flag1 ? this.heightDependentRadiusEllipse(l1, k, i1) : this.heightDependentRadiusRound(random, l1, k, i1);
                    if (flag1 || i2 < k1) {
                        this.generateIcebergBlock(generatoraccessseed, random, blockposition, k, i2, l1, j2, k1, j1, flag1, j, d0, flag, iblockdata);
                    }
                }
            }
        }

        this.smooth(generatoraccessseed, blockposition, i1, k, flag1, i);

        for (i2 = -j1; i2 < j1; ++i2) {
            for (j2 = -j1; j2 < j1; ++j2) {
                for (l1 = -1; l1 > -l; --l1) {
                    k1 = flag1 ? MathHelper.ceil((float) j1 * (1.0F - (float) Math.pow((double) l1, 2.0D) / ((float) l * 8.0F))) : j1;
                    int k2 = this.heightDependentRadiusSteep(random, -l1, l, i1);

                    if (i2 < k2) {
                        this.generateIcebergBlock(generatoraccessseed, random, blockposition, l, i2, l1, j2, k2, k1, flag1, j, d0, flag, iblockdata);
                    }
                }
            }
        }

        boolean flag3 = flag1 ? random.nextDouble() > 0.1D : random.nextDouble() > 0.7D;

        if (flag3) {
            this.generateCutOut(random, generatoraccessseed, i1, k, blockposition, flag1, i, d0, j);
        }

        return true;
    }

    private void generateCutOut(Random random, GeneratorAccess generatoraccess, int i, int j, BlockPosition blockposition, boolean flag, int k, double d0, int l) {
        int i1 = random.nextBoolean() ? -1 : 1;
        int j1 = random.nextBoolean() ? -1 : 1;
        int k1 = random.nextInt(Math.max(i / 2 - 2, 1));

        if (random.nextBoolean()) {
            k1 = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
        }

        int l1 = random.nextInt(Math.max(i / 2 - 2, 1));

        if (random.nextBoolean()) {
            l1 = i / 2 + 1 - random.nextInt(Math.max(i - i / 2 - 1, 1));
        }

        if (flag) {
            k1 = l1 = random.nextInt(Math.max(k - 5, 1));
        }

        BlockPosition blockposition1 = new BlockPosition(i1 * k1, 0, j1 * l1);
        double d1 = flag ? d0 + 1.5707963267948966D : random.nextDouble() * 2.0D * 3.141592653589793D;

        int i2;
        int j2;

        for (j2 = 0; j2 < j - 3; ++j2) {
            i2 = this.heightDependentRadiusRound(random, j2, j, i);
            this.carve(i2, j2, blockposition, generatoraccess, false, d1, blockposition1, k, l);
        }

        for (j2 = -1; j2 > -j + random.nextInt(5); --j2) {
            i2 = this.heightDependentRadiusSteep(random, -j2, j, i);
            this.carve(i2, j2, blockposition, generatoraccess, true, d1, blockposition1, k, l);
        }

    }

    private void carve(int i, int j, BlockPosition blockposition, GeneratorAccess generatoraccess, boolean flag, double d0, BlockPosition blockposition1, int k, int l) {
        int i1 = i + 1 + k / 3;
        int j1 = Math.min(i - 3, 3) + l / 2 - 1;

        for (int k1 = -i1; k1 < i1; ++k1) {
            for (int l1 = -i1; l1 < i1; ++l1) {
                double d1 = this.signedDistanceEllipse(k1, l1, blockposition1, i1, j1, d0);

                if (d1 < 0.0D) {
                    BlockPosition blockposition2 = blockposition.offset(k1, j, l1);
                    IBlockData iblockdata = generatoraccess.getBlockState(blockposition2);

                    if (isIcebergState(iblockdata) || iblockdata.is(Blocks.SNOW_BLOCK)) {
                        if (flag) {
                            this.setBlock(generatoraccess, blockposition2, Blocks.WATER.defaultBlockState());
                        } else {
                            this.setBlock(generatoraccess, blockposition2, Blocks.AIR.defaultBlockState());
                            this.removeFloatingSnowLayer(generatoraccess, blockposition2);
                        }
                    }
                }
            }
        }

    }

    private void removeFloatingSnowLayer(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (generatoraccess.getBlockState(blockposition.above()).is(Blocks.SNOW)) {
            this.setBlock(generatoraccess, blockposition.above(), Blocks.AIR.defaultBlockState());
        }

    }

    private void generateIcebergBlock(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, int i, int j, int k, int l, int i1, int j1, boolean flag, int k1, double d0, boolean flag1, IBlockData iblockdata) {
        double d1 = flag ? this.signedDistanceEllipse(j, l, BlockPosition.ZERO, j1, this.getEllipseC(k, i, k1), d0) : this.signedDistanceCircle(j, l, BlockPosition.ZERO, i1, random);

        if (d1 < 0.0D) {
            BlockPosition blockposition1 = blockposition.offset(j, k, l);
            double d2 = flag ? -0.5D : (double) (-6 - random.nextInt(3));

            if (d1 > d2 && random.nextDouble() > 0.9D) {
                return;
            }

            this.setIcebergBlock(blockposition1, generatoraccess, random, i - k, i, flag, flag1, iblockdata);
        }

    }

    private void setIcebergBlock(BlockPosition blockposition, GeneratorAccess generatoraccess, Random random, int i, int j, boolean flag, boolean flag1, IBlockData iblockdata) {
        IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition);

        if (iblockdata1.getMaterial() == Material.AIR || iblockdata1.is(Blocks.SNOW_BLOCK) || iblockdata1.is(Blocks.ICE) || iblockdata1.is(Blocks.WATER)) {
            boolean flag2 = !flag || random.nextDouble() > 0.05D;
            int k = flag ? 3 : 2;

            if (flag1 && !iblockdata1.is(Blocks.WATER) && (double) i <= (double) random.nextInt(Math.max(1, j / k)) + (double) j * 0.6D && flag2) {
                this.setBlock(generatoraccess, blockposition, Blocks.SNOW_BLOCK.defaultBlockState());
            } else {
                this.setBlock(generatoraccess, blockposition, iblockdata);
            }
        }

    }

    private int getEllipseC(int i, int j, int k) {
        int l = k;

        if (i > 0 && j - i <= 3) {
            l = k - (4 - (j - i));
        }

        return l;
    }

    private double signedDistanceCircle(int i, int j, BlockPosition blockposition, int k, Random random) {
        float f = 10.0F * MathHelper.clamp(random.nextFloat(), 0.2F, 0.8F) / (float) k;

        return (double) f + Math.pow((double) (i - blockposition.getX()), 2.0D) + Math.pow((double) (j - blockposition.getZ()), 2.0D) - Math.pow((double) k, 2.0D);
    }

    private double signedDistanceEllipse(int i, int j, BlockPosition blockposition, int k, int l, double d0) {
        return Math.pow(((double) (i - blockposition.getX()) * Math.cos(d0) - (double) (j - blockposition.getZ()) * Math.sin(d0)) / (double) k, 2.0D) + Math.pow(((double) (i - blockposition.getX()) * Math.sin(d0) + (double) (j - blockposition.getZ()) * Math.cos(d0)) / (double) l, 2.0D) - 1.0D;
    }

    private int heightDependentRadiusRound(Random random, int i, int j, int k) {
        float f = 3.5F - random.nextFloat();
        float f1 = (1.0F - (float) Math.pow((double) i, 2.0D) / ((float) j * f)) * (float) k;

        if (j > 15 + random.nextInt(5)) {
            int l = i < 3 + random.nextInt(6) ? i / 2 : i;

            f1 = (1.0F - (float) l / ((float) j * f * 0.4F)) * (float) k;
        }

        return MathHelper.ceil(f1 / 2.0F);
    }

    private int heightDependentRadiusEllipse(int i, int j, int k) {
        float f = 1.0F;
        float f1 = (1.0F - (float) Math.pow((double) i, 2.0D) / ((float) j * 1.0F)) * (float) k;

        return MathHelper.ceil(f1 / 2.0F);
    }

    private int heightDependentRadiusSteep(Random random, int i, int j, int k) {
        float f = 1.0F + random.nextFloat() / 2.0F;
        float f1 = (1.0F - (float) i / ((float) j * f)) * (float) k;

        return MathHelper.ceil(f1 / 2.0F);
    }

    private static boolean isIcebergState(IBlockData iblockdata) {
        return iblockdata.is(Blocks.PACKED_ICE) || iblockdata.is(Blocks.SNOW_BLOCK) || iblockdata.is(Blocks.BLUE_ICE);
    }

    private boolean belowIsAir(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getBlockState(blockposition.below()).getMaterial() == Material.AIR;
    }

    private void smooth(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j, boolean flag, int k) {
        int l = flag ? k : i / 2;

        for (int i1 = -l; i1 <= l; ++i1) {
            for (int j1 = -l; j1 <= l; ++j1) {
                for (int k1 = 0; k1 <= j; ++k1) {
                    BlockPosition blockposition1 = blockposition.offset(i1, k1, j1);
                    IBlockData iblockdata = generatoraccess.getBlockState(blockposition1);

                    if (isIcebergState(iblockdata) || iblockdata.is(Blocks.SNOW)) {
                        if (this.belowIsAir(generatoraccess, blockposition1)) {
                            this.setBlock(generatoraccess, blockposition1, Blocks.AIR.defaultBlockState());
                            this.setBlock(generatoraccess, blockposition1.above(), Blocks.AIR.defaultBlockState());
                        } else if (isIcebergState(iblockdata)) {
                            IBlockData[] aiblockdata = new IBlockData[]{generatoraccess.getBlockState(blockposition1.west()), generatoraccess.getBlockState(blockposition1.east()), generatoraccess.getBlockState(blockposition1.north()), generatoraccess.getBlockState(blockposition1.south())};
                            int l1 = 0;
                            IBlockData[] aiblockdata1 = aiblockdata;
                            int i2 = aiblockdata.length;

                            for (int j2 = 0; j2 < i2; ++j2) {
                                IBlockData iblockdata1 = aiblockdata1[j2];

                                if (!isIcebergState(iblockdata1)) {
                                    ++l1;
                                }
                            }

                            if (l1 >= 3) {
                                this.setBlock(generatoraccess, blockposition1, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }

    }
}

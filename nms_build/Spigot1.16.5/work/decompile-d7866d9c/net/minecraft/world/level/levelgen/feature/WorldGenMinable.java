package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;

public class WorldGenMinable extends WorldGenerator<WorldGenFeatureOreConfiguration> {

    public WorldGenMinable(Codec<WorldGenFeatureOreConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureOreConfiguration worldgenfeatureoreconfiguration) {
        float f = random.nextFloat() * 3.1415927F;
        float f1 = (float) worldgenfeatureoreconfiguration.c / 8.0F;
        int i = MathHelper.f(((float) worldgenfeatureoreconfiguration.c / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d0 = (double) blockposition.getX() + Math.sin((double) f) * (double) f1;
        double d1 = (double) blockposition.getX() - Math.sin((double) f) * (double) f1;
        double d2 = (double) blockposition.getZ() + Math.cos((double) f) * (double) f1;
        double d3 = (double) blockposition.getZ() - Math.cos((double) f) * (double) f1;
        boolean flag = true;
        double d4 = (double) (blockposition.getY() + random.nextInt(3) - 2);
        double d5 = (double) (blockposition.getY() + random.nextInt(3) - 2);
        int j = blockposition.getX() - MathHelper.f(f1) - i;
        int k = blockposition.getY() - 2 - i;
        int l = blockposition.getZ() - MathHelper.f(f1) - i;
        int i1 = 2 * (MathHelper.f(f1) + i);
        int j1 = 2 * (2 + i);

        for (int k1 = j; k1 <= j + i1; ++k1) {
            for (int l1 = l; l1 <= l + i1; ++l1) {
                if (k <= generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR_WG, k1, l1)) {
                    return this.a(generatoraccessseed, random, worldgenfeatureoreconfiguration, d0, d1, d2, d3, d4, d5, j, k, l, i1, j1);
                }
            }
        }

        return false;
    }

    protected boolean a(GeneratorAccess generatoraccess, Random random, WorldGenFeatureOreConfiguration worldgenfeatureoreconfiguration, double d0, double d1, double d2, double d3, double d4, double d5, int i, int j, int k, int l, int i1) {
        int j1 = 0;
        BitSet bitset = new BitSet(l * i1 * l);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int k1 = worldgenfeatureoreconfiguration.c;
        double[] adouble = new double[k1 * 4];

        int l1;
        double d6;
        double d7;
        double d8;
        double d9;

        for (l1 = 0; l1 < k1; ++l1) {
            float f = (float) l1 / (float) k1;

            d6 = MathHelper.d((double) f, d0, d1);
            d7 = MathHelper.d((double) f, d4, d5);
            d8 = MathHelper.d((double) f, d2, d3);
            d9 = random.nextDouble() * (double) k1 / 16.0D;
            double d10 = ((double) (MathHelper.sin(3.1415927F * f) + 1.0F) * d9 + 1.0D) / 2.0D;

            adouble[l1 * 4 + 0] = d6;
            adouble[l1 * 4 + 1] = d7;
            adouble[l1 * 4 + 2] = d8;
            adouble[l1 * 4 + 3] = d10;
        }

        for (l1 = 0; l1 < k1 - 1; ++l1) {
            if (adouble[l1 * 4 + 3] > 0.0D) {
                for (int i2 = l1 + 1; i2 < k1; ++i2) {
                    if (adouble[i2 * 4 + 3] > 0.0D) {
                        d6 = adouble[l1 * 4 + 0] - adouble[i2 * 4 + 0];
                        d7 = adouble[l1 * 4 + 1] - adouble[i2 * 4 + 1];
                        d8 = adouble[l1 * 4 + 2] - adouble[i2 * 4 + 2];
                        d9 = adouble[l1 * 4 + 3] - adouble[i2 * 4 + 3];
                        if (d9 * d9 > d6 * d6 + d7 * d7 + d8 * d8) {
                            if (d9 > 0.0D) {
                                adouble[i2 * 4 + 3] = -1.0D;
                            } else {
                                adouble[l1 * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (l1 = 0; l1 < k1; ++l1) {
            double d11 = adouble[l1 * 4 + 3];

            if (d11 >= 0.0D) {
                double d12 = adouble[l1 * 4 + 0];
                double d13 = adouble[l1 * 4 + 1];
                double d14 = adouble[l1 * 4 + 2];
                int j2 = Math.max(MathHelper.floor(d12 - d11), i);
                int k2 = Math.max(MathHelper.floor(d13 - d11), j);
                int l2 = Math.max(MathHelper.floor(d14 - d11), k);
                int i3 = Math.max(MathHelper.floor(d12 + d11), j2);
                int j3 = Math.max(MathHelper.floor(d13 + d11), k2);
                int k3 = Math.max(MathHelper.floor(d14 + d11), l2);

                for (int l3 = j2; l3 <= i3; ++l3) {
                    double d15 = ((double) l3 + 0.5D - d12) / d11;

                    if (d15 * d15 < 1.0D) {
                        for (int i4 = k2; i4 <= j3; ++i4) {
                            double d16 = ((double) i4 + 0.5D - d13) / d11;

                            if (d15 * d15 + d16 * d16 < 1.0D) {
                                for (int j4 = l2; j4 <= k3; ++j4) {
                                    double d17 = ((double) j4 + 0.5D - d14) / d11;

                                    if (d15 * d15 + d16 * d16 + d17 * d17 < 1.0D) {
                                        int k4 = l3 - i + (i4 - j) * l + (j4 - k) * l * i1;

                                        if (!bitset.get(k4)) {
                                            bitset.set(k4);
                                            blockposition_mutableblockposition.d(l3, i4, j4);
                                            if (worldgenfeatureoreconfiguration.b.a(generatoraccess.getType(blockposition_mutableblockposition), random)) {
                                                generatoraccess.setTypeAndData(blockposition_mutableblockposition, worldgenfeatureoreconfiguration.d, 2);
                                                ++j1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return j1 > 0;
    }
}

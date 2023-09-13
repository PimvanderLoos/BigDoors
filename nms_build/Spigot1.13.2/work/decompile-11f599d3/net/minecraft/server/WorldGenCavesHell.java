package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;

public class WorldGenCavesHell extends WorldGenCaves {

    public WorldGenCavesHell() {
        this.e = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[] { Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK});
        this.f = ImmutableSet.of(FluidTypes.LAVA, FluidTypes.WATER);
    }

    public boolean a(IBlockAccess iblockaccess, Random random, int i, int j, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
        return random.nextFloat() <= worldgenfeatureconfigurationchance.a;
    }

    public boolean a(GeneratorAccess generatoraccess, Random random, int i, int j, int k, int l, BitSet bitset, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
        int i1 = (this.a() * 2 - 1) * 16;
        int j1 = random.nextInt(random.nextInt(random.nextInt(10) + 1) + 1);

        for (int k1 = 0; k1 < j1; ++k1) {
            double d0 = (double) (i * 16 + random.nextInt(16));
            double d1 = (double) random.nextInt(128);
            double d2 = (double) (j * 16 + random.nextInt(16));
            int l1 = 1;
            float f;

            if (random.nextInt(4) == 0) {
                double d3 = 0.5D;

                f = 1.0F + random.nextFloat() * 6.0F;
                this.a(generatoraccess, random.nextLong(), k, l, d0, d1, d2, f, 0.5D, bitset);
                l1 += random.nextInt(4);
            }

            for (int i2 = 0; i2 < l1; ++i2) {
                float f1 = random.nextFloat() * 6.2831855F;

                f = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
                double d4 = 5.0D;
                float f2 = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
                int j2 = i1 - random.nextInt(i1 / 4);
                boolean flag = false;

                this.a(generatoraccess, random.nextLong(), k, l, d0, d1, d2, f2, f1, f, 0, j2, 5.0D, bitset);
            }
        }

        return true;
    }

    protected boolean a(GeneratorAccess generatoraccess, long i, int j, int k, double d0, double d1, double d2, double d3, double d4, BitSet bitset) {
        double d5 = (double) (j * 16 + 8);
        double d6 = (double) (k * 16 + 8);

        if (d0 >= d5 - 16.0D - d3 * 2.0D && d2 >= d6 - 16.0D - d3 * 2.0D && d0 <= d5 + 16.0D + d3 * 2.0D && d2 <= d6 + 16.0D + d3 * 2.0D) {
            int l = Math.max(MathHelper.floor(d0 - d3) - j * 16 - 1, 0);
            int i1 = Math.min(MathHelper.floor(d0 + d3) - j * 16 + 1, 16);
            int j1 = Math.max(MathHelper.floor(d1 - d4) - 1, 1);
            int k1 = Math.min(MathHelper.floor(d1 + d4) + 1, 120);
            int l1 = Math.max(MathHelper.floor(d2 - d3) - k * 16 - 1, 0);
            int i2 = Math.min(MathHelper.floor(d2 + d3) - k * 16 + 1, 16);

            if (this.a(generatoraccess, j, k, l, i1, j1, k1, l1, i2)) {
                return false;
            } else if (l <= i1 && j1 <= k1 && l1 <= i2) {
                boolean flag = false;

                for (int j2 = l; j2 < i1; ++j2) {
                    int k2 = j2 + j * 16;
                    double d7 = ((double) k2 + 0.5D - d0) / d3;

                    for (int l2 = l1; l2 < i2; ++l2) {
                        int i3 = l2 + k * 16;
                        double d8 = ((double) i3 + 0.5D - d2) / d3;

                        for (int j3 = k1; j3 > j1; --j3) {
                            double d9 = ((double) (j3 - 1) + 0.5D - d1) / d4;

                            if (d9 > -0.7D && d7 * d7 + d9 * d9 + d8 * d8 < 1.0D) {
                                int k3 = j2 | l2 << 4 | j3 << 8;

                                if (!bitset.get(k3)) {
                                    bitset.set(k3);
                                    if (this.a(generatoraccess.getType(new BlockPosition(k2, j3, i3)))) {
                                        if (j3 <= 31) {
                                            generatoraccess.setTypeAndData(new BlockPosition(k2, j3, i3), WorldGenCavesHell.d.i(), 2);
                                        } else {
                                            generatoraccess.setTypeAndData(new BlockPosition(k2, j3, i3), WorldGenCavesHell.b, 2);
                                        }

                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }

                return flag;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

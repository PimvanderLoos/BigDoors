package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public interface Aquifer {

    int ALWAYS_LAVA_AT_OR_BELOW_Y_INDEX = 9;
    int ALWAYS_USE_SEA_LEVEL_WHEN_ABOVE = 30;

    static Aquifer a(ChunkCoordIntPair chunkcoordintpair, NoiseGeneratorNormal noisegeneratornormal, NoiseGeneratorNormal noisegeneratornormal1, NoiseGeneratorNormal noisegeneratornormal2, GeneratorSettingBase generatorsettingbase, NoiseSampler noisesampler, int i, int j) {
        return new Aquifer.a(chunkcoordintpair, noisegeneratornormal, noisegeneratornormal1, noisegeneratornormal2, generatorsettingbase, noisesampler, i, j);
    }

    static Aquifer a(final int i, final IBlockData iblockdata) {
        return new Aquifer() {
            @Override
            public IBlockData a(BaseStoneSource basestonesource, int j, int k, int l, double d0) {
                return d0 > 0.0D ? basestonesource.getBaseBlock(j, k, l) : (k >= i ? Blocks.AIR.getBlockData() : iblockdata);
            }

            @Override
            public boolean a() {
                return false;
            }
        };
    }

    IBlockData a(BaseStoneSource basestonesource, int i, int j, int k, double d0);

    boolean a();

    public static class a implements Aquifer {

        private static final int X_RANGE = 10;
        private static final int Y_RANGE = 9;
        private static final int Z_RANGE = 10;
        private static final int X_SEPARATION = 6;
        private static final int Y_SEPARATION = 3;
        private static final int Z_SEPARATION = 6;
        private static final int X_SPACING = 16;
        private static final int Y_SPACING = 12;
        private static final int Z_SPACING = 16;
        private final NoiseGeneratorNormal barrierNoise;
        private final NoiseGeneratorNormal waterLevelNoise;
        private final NoiseGeneratorNormal lavaNoise;
        private final GeneratorSettingBase noiseGeneratorSettings;
        private final Aquifer.a.a[] aquiferCache;
        private final long[] aquiferLocationCache;
        private boolean shouldScheduleFluidUpdate;
        private final NoiseSampler sampler;
        private final int minGridX;
        private final int minGridY;
        private final int minGridZ;
        private final int gridSizeX;
        private final int gridSizeZ;

        a(ChunkCoordIntPair chunkcoordintpair, NoiseGeneratorNormal noisegeneratornormal, NoiseGeneratorNormal noisegeneratornormal1, NoiseGeneratorNormal noisegeneratornormal2, GeneratorSettingBase generatorsettingbase, NoiseSampler noisesampler, int i, int j) {
            this.barrierNoise = noisegeneratornormal;
            this.waterLevelNoise = noisegeneratornormal1;
            this.lavaNoise = noisegeneratornormal2;
            this.noiseGeneratorSettings = generatorsettingbase;
            this.sampler = noisesampler;
            this.minGridX = this.b(chunkcoordintpair.d()) - 1;
            int k = this.b(chunkcoordintpair.f()) + 1;

            this.gridSizeX = k - this.minGridX + 1;
            this.minGridY = this.c(i) - 1;
            int l = this.c(i + j) + 1;
            int i1 = l - this.minGridY + 1;

            this.minGridZ = this.d(chunkcoordintpair.e()) - 1;
            int j1 = this.d(chunkcoordintpair.g()) + 1;

            this.gridSizeZ = j1 - this.minGridZ + 1;
            int k1 = this.gridSizeX * i1 * this.gridSizeZ;

            this.aquiferCache = new Aquifer.a.a[k1];
            this.aquiferLocationCache = new long[k1];
            Arrays.fill(this.aquiferLocationCache, Long.MAX_VALUE);
        }

        private int a(int i, int j, int k) {
            int l = i - this.minGridX;
            int i1 = j - this.minGridY;
            int j1 = k - this.minGridZ;

            return (i1 * this.gridSizeZ + j1) * this.gridSizeX + l;
        }

        @Override
        public IBlockData a(BaseStoneSource basestonesource, int i, int j, int k, double d0) {
            if (d0 <= 0.0D) {
                IBlockData iblockdata;
                double d1;
                boolean flag;

                if (this.a(j)) {
                    iblockdata = Blocks.LAVA.getBlockData();
                    d1 = 0.0D;
                    flag = false;
                } else {
                    int l = Math.floorDiv(i - 5, 16);
                    int i1 = Math.floorDiv(j + 1, 12);
                    int j1 = Math.floorDiv(k - 5, 16);
                    int k1 = Integer.MAX_VALUE;
                    int l1 = Integer.MAX_VALUE;
                    int i2 = Integer.MAX_VALUE;
                    long j2 = 0L;
                    long k2 = 0L;
                    long l2 = 0L;

                    for (int i3 = 0; i3 <= 1; ++i3) {
                        for (int j3 = -1; j3 <= 1; ++j3) {
                            for (int k3 = 0; k3 <= 1; ++k3) {
                                int l3 = l + i3;
                                int i4 = i1 + j3;
                                int j4 = j1 + k3;
                                int k4 = this.a(l3, i4, j4);
                                long l4 = this.aquiferLocationCache[k4];
                                long i5;

                                if (l4 != Long.MAX_VALUE) {
                                    i5 = l4;
                                } else {
                                    SeededRandom seededrandom = new SeededRandom(MathHelper.c(l3, i4 * 3, j4) + 1L);

                                    i5 = BlockPosition.a(l3 * 16 + seededrandom.nextInt(10), i4 * 12 + seededrandom.nextInt(9), j4 * 16 + seededrandom.nextInt(10));
                                    this.aquiferLocationCache[k4] = i5;
                                }

                                int j5 = BlockPosition.a(i5) - i;
                                int k5 = BlockPosition.b(i5) - j;
                                int l5 = BlockPosition.c(i5) - k;
                                int i6 = j5 * j5 + k5 * k5 + l5 * l5;

                                if (k1 >= i6) {
                                    l2 = k2;
                                    k2 = j2;
                                    j2 = i5;
                                    i2 = l1;
                                    l1 = k1;
                                    k1 = i6;
                                } else if (l1 >= i6) {
                                    l2 = k2;
                                    k2 = i5;
                                    i2 = l1;
                                    l1 = i6;
                                } else if (i2 >= i6) {
                                    l2 = i5;
                                    i2 = i6;
                                }
                            }
                        }
                    }

                    Aquifer.a.a aquifer_a_a = this.a(j2);
                    Aquifer.a.a aquifer_a_a1 = this.a(k2);
                    Aquifer.a.a aquifer_a_a2 = this.a(l2);
                    double d2 = this.a(k1, l1);
                    double d3 = this.a(k1, i2);
                    double d4 = this.a(l1, i2);

                    flag = d2 > 0.0D;
                    if (aquifer_a_a.fluidLevel >= j && aquifer_a_a.fluidType.a(Blocks.WATER) && this.a(j - 1)) {
                        d1 = 1.0D;
                    } else if (d2 > -1.0D) {
                        double d5 = 1.0D + (this.barrierNoise.a((double) i, (double) j, (double) k) + 0.05D) / 4.0D;
                        double d6 = this.a(j, d5, aquifer_a_a, aquifer_a_a1);
                        double d7 = this.a(j, d5, aquifer_a_a, aquifer_a_a2);
                        double d8 = this.a(j, d5, aquifer_a_a1, aquifer_a_a2);
                        double d9 = Math.max(0.0D, d2);
                        double d10 = Math.max(0.0D, d3);
                        double d11 = Math.max(0.0D, d4);
                        double d12 = 2.0D * d9 * Math.max(d6, Math.max(d7 * d10, d8 * d11));

                        d1 = Math.max(0.0D, d12);
                    } else {
                        d1 = 0.0D;
                    }

                    iblockdata = j >= aquifer_a_a.fluidLevel ? Blocks.AIR.getBlockData() : aquifer_a_a.fluidType;
                }

                if (d0 + d1 <= 0.0D) {
                    this.shouldScheduleFluidUpdate = flag;
                    return iblockdata;
                }
            }

            this.shouldScheduleFluidUpdate = false;
            return basestonesource.getBaseBlock(i, j, k);
        }

        @Override
        public boolean a() {
            return this.shouldScheduleFluidUpdate;
        }

        private boolean a(int i) {
            return i - this.noiseGeneratorSettings.b().a() <= 9;
        }

        private double a(int i, int j) {
            double d0 = 25.0D;

            return 1.0D - (double) Math.abs(j - i) / 25.0D;
        }

        private double a(int i, double d0, Aquifer.a.a aquifer_a_a, Aquifer.a.a aquifer_a_a1) {
            if (i <= aquifer_a_a.fluidLevel && i <= aquifer_a_a1.fluidLevel && aquifer_a_a.fluidType != aquifer_a_a1.fluidType) {
                return 1.0D;
            } else {
                int j = Math.abs(aquifer_a_a.fluidLevel - aquifer_a_a1.fluidLevel);
                double d1 = 0.5D * (double) (aquifer_a_a.fluidLevel + aquifer_a_a1.fluidLevel);
                double d2 = Math.abs(d1 - (double) i - 0.5D);

                return 0.5D * (double) j * d0 - d2;
            }
        }

        private int b(int i) {
            return Math.floorDiv(i, 16);
        }

        private int c(int i) {
            return Math.floorDiv(i, 12);
        }

        private int d(int i) {
            return Math.floorDiv(i, 16);
        }

        private Aquifer.a.a a(long i) {
            int j = BlockPosition.a(i);
            int k = BlockPosition.b(i);
            int l = BlockPosition.c(i);
            int i1 = this.b(j);
            int j1 = this.c(k);
            int k1 = this.d(l);
            int l1 = this.a(i1, j1, k1);
            Aquifer.a.a aquifer_a_a = this.aquiferCache[l1];

            if (aquifer_a_a != null) {
                return aquifer_a_a;
            } else {
                Aquifer.a.a aquifer_a_a1 = this.b(j, k, l);

                this.aquiferCache[l1] = aquifer_a_a1;
                return aquifer_a_a1;
            }
        }

        private Aquifer.a.a b(int i, int j, int k) {
            int l = this.noiseGeneratorSettings.g();

            if (j > 30) {
                return new Aquifer.a.a(l, Blocks.WATER.getBlockData());
            } else {
                boolean flag = true;
                boolean flag1 = true;
                boolean flag2 = true;
                double d0 = this.waterLevelNoise.a((double) Math.floorDiv(i, 64), (double) Math.floorDiv(j, 40) / 1.4D, (double) Math.floorDiv(k, 64)) * 30.0D + -10.0D;
                boolean flag3 = false;

                if (Math.abs(d0) > 8.0D) {
                    d0 *= 4.0D;
                }

                int i1 = Math.floorDiv(j, 40) * 40 + 20;
                int j1 = i1 + MathHelper.floor(d0);

                if (i1 == -20) {
                    double d1 = this.lavaNoise.a((double) Math.floorDiv(i, 64), (double) Math.floorDiv(j, 40) / 1.4D, (double) Math.floorDiv(k, 64));

                    flag3 = Math.abs(d1) > 0.2199999988079071D;
                }

                return new Aquifer.a.a(Math.min(56, j1), flag3 ? Blocks.LAVA.getBlockData() : Blocks.WATER.getBlockData());
            }
        }

        private static final class a {

            final int fluidLevel;
            final IBlockData fluidType;

            public a(int i, IBlockData iblockdata) {
                this.fluidLevel = i;
                this.fluidType = iblockdata;
            }
        }
    }
}

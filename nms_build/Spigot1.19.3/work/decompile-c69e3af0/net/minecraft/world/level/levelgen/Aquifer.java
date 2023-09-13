package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface Aquifer {

    static Aquifer create(NoiseChunk noisechunk, ChunkCoordIntPair chunkcoordintpair, NoiseRouter noiserouter, PositionalRandomFactory positionalrandomfactory, int i, int j, Aquifer.a aquifer_a) {
        return new Aquifer.c(noisechunk, chunkcoordintpair, noiserouter, positionalrandomfactory, i, j, aquifer_a);
    }

    static Aquifer createDisabled(final Aquifer.a aquifer_a) {
        return new Aquifer() {
            @Nullable
            @Override
            public IBlockData computeSubstance(DensityFunction.b densityfunction_b, double d0) {
                return d0 > 0.0D ? null : aquifer_a.computeFluid(densityfunction_b.blockX(), densityfunction_b.blockY(), densityfunction_b.blockZ()).at(densityfunction_b.blockY());
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return false;
            }
        };
    }

    @Nullable
    IBlockData computeSubstance(DensityFunction.b densityfunction_b, double d0);

    boolean shouldScheduleFluidUpdate();

    public static class c implements Aquifer {

        private static final int X_RANGE = 10;
        private static final int Y_RANGE = 9;
        private static final int Z_RANGE = 10;
        private static final int X_SEPARATION = 6;
        private static final int Y_SEPARATION = 3;
        private static final int Z_SEPARATION = 6;
        private static final int X_SPACING = 16;
        private static final int Y_SPACING = 12;
        private static final int Z_SPACING = 16;
        private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
        private static final double FLOWING_UPDATE_SIMULARITY = similarity(MathHelper.square(10), MathHelper.square(12));
        private final NoiseChunk noiseChunk;
        private final DensityFunction barrierNoise;
        private final DensityFunction fluidLevelFloodednessNoise;
        private final DensityFunction fluidLevelSpreadNoise;
        private final DensityFunction lavaNoise;
        private final PositionalRandomFactory positionalRandomFactory;
        private final Aquifer.b[] aquiferCache;
        private final long[] aquiferLocationCache;
        private final Aquifer.a globalFluidPicker;
        private final DensityFunction erosion;
        private final DensityFunction depth;
        private boolean shouldScheduleFluidUpdate;
        private final int minGridX;
        private final int minGridY;
        private final int minGridZ;
        private final int gridSizeX;
        private final int gridSizeZ;
        private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

        c(NoiseChunk noisechunk, ChunkCoordIntPair chunkcoordintpair, NoiseRouter noiserouter, PositionalRandomFactory positionalrandomfactory, int i, int j, Aquifer.a aquifer_a) {
            this.noiseChunk = noisechunk;
            this.barrierNoise = noiserouter.barrierNoise();
            this.fluidLevelFloodednessNoise = noiserouter.fluidLevelFloodednessNoise();
            this.fluidLevelSpreadNoise = noiserouter.fluidLevelSpreadNoise();
            this.lavaNoise = noiserouter.lavaNoise();
            this.erosion = noiserouter.erosion();
            this.depth = noiserouter.depth();
            this.positionalRandomFactory = positionalrandomfactory;
            this.minGridX = this.gridX(chunkcoordintpair.getMinBlockX()) - 1;
            this.globalFluidPicker = aquifer_a;
            int k = this.gridX(chunkcoordintpair.getMaxBlockX()) + 1;

            this.gridSizeX = k - this.minGridX + 1;
            this.minGridY = this.gridY(i) - 1;
            int l = this.gridY(i + j) + 1;
            int i1 = l - this.minGridY + 1;

            this.minGridZ = this.gridZ(chunkcoordintpair.getMinBlockZ()) - 1;
            int j1 = this.gridZ(chunkcoordintpair.getMaxBlockZ()) + 1;

            this.gridSizeZ = j1 - this.minGridZ + 1;
            int k1 = this.gridSizeX * i1 * this.gridSizeZ;

            this.aquiferCache = new Aquifer.b[k1];
            this.aquiferLocationCache = new long[k1];
            Arrays.fill(this.aquiferLocationCache, Long.MAX_VALUE);
        }

        private int getIndex(int i, int j, int k) {
            int l = i - this.minGridX;
            int i1 = j - this.minGridY;
            int j1 = k - this.minGridZ;

            return (i1 * this.gridSizeZ + j1) * this.gridSizeX + l;
        }

        @Nullable
        @Override
        public IBlockData computeSubstance(DensityFunction.b densityfunction_b, double d0) {
            int i = densityfunction_b.blockX();
            int j = densityfunction_b.blockY();
            int k = densityfunction_b.blockZ();

            if (d0 > 0.0D) {
                this.shouldScheduleFluidUpdate = false;
                return null;
            } else {
                Aquifer.b aquifer_b = this.globalFluidPicker.computeFluid(i, j, k);

                if (aquifer_b.at(j).is(Blocks.LAVA)) {
                    this.shouldScheduleFluidUpdate = false;
                    return Blocks.LAVA.defaultBlockState();
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
                                int k4 = this.getIndex(l3, i4, j4);
                                long l4 = this.aquiferLocationCache[k4];
                                long i5;

                                if (l4 != Long.MAX_VALUE) {
                                    i5 = l4;
                                } else {
                                    RandomSource randomsource = this.positionalRandomFactory.at(l3, i4, j4);

                                    i5 = BlockPosition.asLong(l3 * 16 + randomsource.nextInt(10), i4 * 12 + randomsource.nextInt(9), j4 * 16 + randomsource.nextInt(10));
                                    this.aquiferLocationCache[k4] = i5;
                                }

                                int j5 = BlockPosition.getX(i5) - i;
                                int k5 = BlockPosition.getY(i5) - j;
                                int l5 = BlockPosition.getZ(i5) - k;
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

                    Aquifer.b aquifer_b1 = this.getAquiferStatus(j2);
                    double d1 = similarity(k1, l1);
                    IBlockData iblockdata = aquifer_b1.at(j);

                    if (d1 <= 0.0D) {
                        this.shouldScheduleFluidUpdate = d1 >= Aquifer.c.FLOWING_UPDATE_SIMULARITY;
                        return iblockdata;
                    } else if (iblockdata.is(Blocks.WATER) && this.globalFluidPicker.computeFluid(i, j - 1, k).at(j - 1).is(Blocks.LAVA)) {
                        this.shouldScheduleFluidUpdate = true;
                        return iblockdata;
                    } else {
                        MutableDouble mutabledouble = new MutableDouble(Double.NaN);
                        Aquifer.b aquifer_b2 = this.getAquiferStatus(k2);
                        double d2 = d1 * this.calculatePressure(densityfunction_b, mutabledouble, aquifer_b1, aquifer_b2);

                        if (d0 + d2 > 0.0D) {
                            this.shouldScheduleFluidUpdate = false;
                            return null;
                        } else {
                            Aquifer.b aquifer_b3 = this.getAquiferStatus(l2);
                            double d3 = similarity(k1, i2);
                            double d4;

                            if (d3 > 0.0D) {
                                d4 = d1 * d3 * this.calculatePressure(densityfunction_b, mutabledouble, aquifer_b1, aquifer_b3);
                                if (d0 + d4 > 0.0D) {
                                    this.shouldScheduleFluidUpdate = false;
                                    return null;
                                }
                            }

                            d4 = similarity(l1, i2);
                            if (d4 > 0.0D) {
                                double d5 = d1 * d4 * this.calculatePressure(densityfunction_b, mutabledouble, aquifer_b2, aquifer_b3);

                                if (d0 + d5 > 0.0D) {
                                    this.shouldScheduleFluidUpdate = false;
                                    return null;
                                }
                            }

                            this.shouldScheduleFluidUpdate = true;
                            return iblockdata;
                        }
                    }
                }
            }
        }

        @Override
        public boolean shouldScheduleFluidUpdate() {
            return this.shouldScheduleFluidUpdate;
        }

        private static double similarity(int i, int j) {
            double d0 = 25.0D;

            return 1.0D - (double) Math.abs(j - i) / 25.0D;
        }

        private double calculatePressure(DensityFunction.b densityfunction_b, MutableDouble mutabledouble, Aquifer.b aquifer_b, Aquifer.b aquifer_b1) {
            int i = densityfunction_b.blockY();
            IBlockData iblockdata = aquifer_b.at(i);
            IBlockData iblockdata1 = aquifer_b1.at(i);

            if ((!iblockdata.is(Blocks.LAVA) || !iblockdata1.is(Blocks.WATER)) && (!iblockdata.is(Blocks.WATER) || !iblockdata1.is(Blocks.LAVA))) {
                int j = Math.abs(aquifer_b.fluidLevel - aquifer_b1.fluidLevel);

                if (j == 0) {
                    return 0.0D;
                } else {
                    double d0 = 0.5D * (double) (aquifer_b.fluidLevel + aquifer_b1.fluidLevel);
                    double d1 = (double) i + 0.5D - d0;
                    double d2 = (double) j / 2.0D;
                    double d3 = 0.0D;
                    double d4 = 2.5D;
                    double d5 = 1.5D;
                    double d6 = 3.0D;
                    double d7 = 10.0D;
                    double d8 = 3.0D;
                    double d9 = d2 - Math.abs(d1);
                    double d10;
                    double d11;

                    if (d1 > 0.0D) {
                        d10 = 0.0D + d9;
                        if (d10 > 0.0D) {
                            d11 = d10 / 1.5D;
                        } else {
                            d11 = d10 / 2.5D;
                        }
                    } else {
                        d10 = 3.0D + d9;
                        if (d10 > 0.0D) {
                            d11 = d10 / 3.0D;
                        } else {
                            d11 = d10 / 10.0D;
                        }
                    }

                    d10 = 2.0D;
                    double d12;

                    if (d11 >= -2.0D && d11 <= 2.0D) {
                        double d13 = mutabledouble.getValue();

                        if (Double.isNaN(d13)) {
                            double d14 = this.barrierNoise.compute(densityfunction_b);

                            mutabledouble.setValue(d14);
                            d12 = d14;
                        } else {
                            d12 = d13;
                        }
                    } else {
                        d12 = 0.0D;
                    }

                    return 2.0D * (d12 + d11);
                }
            } else {
                return 2.0D;
            }
        }

        private int gridX(int i) {
            return Math.floorDiv(i, 16);
        }

        private int gridY(int i) {
            return Math.floorDiv(i, 12);
        }

        private int gridZ(int i) {
            return Math.floorDiv(i, 16);
        }

        private Aquifer.b getAquiferStatus(long i) {
            int j = BlockPosition.getX(i);
            int k = BlockPosition.getY(i);
            int l = BlockPosition.getZ(i);
            int i1 = this.gridX(j);
            int j1 = this.gridY(k);
            int k1 = this.gridZ(l);
            int l1 = this.getIndex(i1, j1, k1);
            Aquifer.b aquifer_b = this.aquiferCache[l1];

            if (aquifer_b != null) {
                return aquifer_b;
            } else {
                Aquifer.b aquifer_b1 = this.computeFluid(j, k, l);

                this.aquiferCache[l1] = aquifer_b1;
                return aquifer_b1;
            }
        }

        private Aquifer.b computeFluid(int i, int j, int k) {
            Aquifer.b aquifer_b = this.globalFluidPicker.computeFluid(i, j, k);
            int l = Integer.MAX_VALUE;
            int i1 = j + 12;
            int j1 = j - 12;
            boolean flag = false;
            int[][] aint = Aquifer.c.SURFACE_SAMPLING_OFFSETS_IN_CHUNKS;
            int k1 = aint.length;

            for (int l1 = 0; l1 < k1; ++l1) {
                int[] aint1 = aint[l1];
                int i2 = i + SectionPosition.sectionToBlockCoord(aint1[0]);
                int j2 = k + SectionPosition.sectionToBlockCoord(aint1[1]);
                int k2 = this.noiseChunk.preliminarySurfaceLevel(i2, j2);
                int l2 = k2 + 8;
                boolean flag1 = aint1[0] == 0 && aint1[1] == 0;

                if (flag1 && j1 > l2) {
                    return aquifer_b;
                }

                boolean flag2 = i1 > l2;

                if (flag2 || flag1) {
                    Aquifer.b aquifer_b1 = this.globalFluidPicker.computeFluid(i2, l2, j2);

                    if (!aquifer_b1.at(l2).isAir()) {
                        if (flag1) {
                            flag = true;
                        }

                        if (flag2) {
                            return aquifer_b1;
                        }
                    }
                }

                l = Math.min(l, k2);
            }

            int i3 = this.computeSurfaceLevel(i, j, k, aquifer_b, l, flag);

            return new Aquifer.b(i3, this.computeFluidType(i, j, k, aquifer_b, i3));
        }

        private int computeSurfaceLevel(int i, int j, int k, Aquifer.b aquifer_b, int l, boolean flag) {
            DensityFunction.e densityfunction_e = new DensityFunction.e(i, j, k);
            double d0;
            double d1;
            int i1;

            if (OverworldBiomeBuilder.isDeepDarkRegion(this.erosion, this.depth, densityfunction_e)) {
                d0 = -1.0D;
                d1 = -1.0D;
            } else {
                i1 = l + 8 - j;
                boolean flag1 = true;
                double d2 = flag ? MathHelper.clampedMap((double) i1, 0.0D, 64.0D, 1.0D, 0.0D) : 0.0D;
                double d3 = MathHelper.clamp(this.fluidLevelFloodednessNoise.compute(densityfunction_e), -1.0D, 1.0D);
                double d4 = MathHelper.map(d2, 1.0D, 0.0D, -0.3D, 0.8D);
                double d5 = MathHelper.map(d2, 1.0D, 0.0D, -0.8D, 0.4D);

                d0 = d3 - d5;
                d1 = d3 - d4;
            }

            if (d1 > 0.0D) {
                i1 = aquifer_b.fluidLevel;
            } else if (d0 > 0.0D) {
                i1 = this.computeRandomizedFluidSurfaceLevel(i, j, k, l);
            } else {
                i1 = DimensionManager.WAY_BELOW_MIN_Y;
            }

            return i1;
        }

        private int computeRandomizedFluidSurfaceLevel(int i, int j, int k, int l) {
            boolean flag = true;
            boolean flag1 = true;
            int i1 = Math.floorDiv(i, 16);
            int j1 = Math.floorDiv(j, 40);
            int k1 = Math.floorDiv(k, 16);
            int l1 = j1 * 40 + 20;
            boolean flag2 = true;
            double d0 = this.fluidLevelSpreadNoise.compute(new DensityFunction.e(i1, j1, k1)) * 10.0D;
            int i2 = MathHelper.quantize(d0, 3);
            int j2 = l1 + i2;

            return Math.min(l, j2);
        }

        private IBlockData computeFluidType(int i, int j, int k, Aquifer.b aquifer_b, int l) {
            IBlockData iblockdata = aquifer_b.fluidType;

            if (l <= -10 && l != DimensionManager.WAY_BELOW_MIN_Y && aquifer_b.fluidType != Blocks.LAVA.defaultBlockState()) {
                boolean flag = true;
                boolean flag1 = true;
                int i1 = Math.floorDiv(i, 64);
                int j1 = Math.floorDiv(j, 40);
                int k1 = Math.floorDiv(k, 64);
                double d0 = this.lavaNoise.compute(new DensityFunction.e(i1, j1, k1));

                if (Math.abs(d0) > 0.3D) {
                    iblockdata = Blocks.LAVA.defaultBlockState();
                }
            }

            return iblockdata;
        }
    }

    public interface a {

        Aquifer.b computeFluid(int i, int j, int k);
    }

    public static final class b {

        final int fluidLevel;
        final IBlockData fluidType;

        public b(int i, IBlockData iblockdata) {
            this.fluidLevel = i;
            this.fluidType = iblockdata;
        }

        public IBlockData at(int i) {
            return i < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
        }
    }
}

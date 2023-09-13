package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface Aquifer {

    static Aquifer create(NoiseChunk noisechunk, ChunkCoordIntPair chunkcoordintpair, NoiseGeneratorNormal noisegeneratornormal, NoiseGeneratorNormal noisegeneratornormal1, NoiseGeneratorNormal noisegeneratornormal2, NoiseGeneratorNormal noisegeneratornormal3, PositionalRandomFactory positionalrandomfactory, int i, int j, Aquifer.a aquifer_a) {
        return new Aquifer.c(noisechunk, chunkcoordintpair, noisegeneratornormal, noisegeneratornormal1, noisegeneratornormal2, noisegeneratornormal3, positionalrandomfactory, i, j, aquifer_a);
    }

    static Aquifer createDisabled(final Aquifer.a aquifer_a) {
        return new Aquifer() {
            @Nullable
            @Override
            public IBlockData computeSubstance(int i, int j, int k, double d0, double d1) {
                return d1 > 0.0D ? null : aquifer_a.computeFluid(i, j, k).at(j);
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return false;
            }
        };
    }

    @Nullable
    IBlockData computeSubstance(int i, int j, int k, double d0, double d1);

    boolean shouldScheduleFluidUpdate();

    public static class c implements Aquifer, Aquifer.a {

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
        private final NoiseGeneratorNormal barrierNoise;
        private final NoiseGeneratorNormal fluidLevelFloodednessNoise;
        private final NoiseGeneratorNormal fluidLevelSpreadNoise;
        private final NoiseGeneratorNormal lavaNoise;
        private final PositionalRandomFactory positionalRandomFactory;
        private final Aquifer.b[] aquiferCache;
        private final long[] aquiferLocationCache;
        private final Aquifer.a globalFluidPicker;
        private boolean shouldScheduleFluidUpdate;
        private final int minGridX;
        private final int minGridY;
        private final int minGridZ;
        private final int gridSizeX;
        private final int gridSizeZ;
        private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

        c(NoiseChunk noisechunk, ChunkCoordIntPair chunkcoordintpair, NoiseGeneratorNormal noisegeneratornormal, NoiseGeneratorNormal noisegeneratornormal1, NoiseGeneratorNormal noisegeneratornormal2, NoiseGeneratorNormal noisegeneratornormal3, PositionalRandomFactory positionalrandomfactory, int i, int j, Aquifer.a aquifer_a) {
            this.noiseChunk = noisechunk;
            this.barrierNoise = noisegeneratornormal;
            this.fluidLevelFloodednessNoise = noisegeneratornormal1;
            this.fluidLevelSpreadNoise = noisegeneratornormal2;
            this.lavaNoise = noisegeneratornormal3;
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
        public IBlockData computeSubstance(int i, int j, int k, double d0, double d1) {
            if (d0 <= -64.0D) {
                return this.globalFluidPicker.computeFluid(i, j, k).at(j);
            } else {
                if (d1 <= 0.0D) {
                    Aquifer.b aquifer_b = this.globalFluidPicker.computeFluid(i, j, k);
                    IBlockData iblockdata;
                    double d2;
                    boolean flag;

                    if (aquifer_b.at(j).is(Blocks.LAVA)) {
                        iblockdata = Blocks.LAVA.defaultBlockState();
                        d2 = 0.0D;
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
                        Aquifer.b aquifer_b2 = this.getAquiferStatus(k2);
                        Aquifer.b aquifer_b3 = this.getAquiferStatus(l2);
                        double d3 = similarity(k1, l1);
                        double d4 = similarity(k1, i2);
                        double d5 = similarity(l1, i2);

                        flag = d3 >= Aquifer.c.FLOWING_UPDATE_SIMULARITY;
                        if (aquifer_b1.at(j).is(Blocks.WATER) && this.globalFluidPicker.computeFluid(i, j - 1, k).at(j - 1).is(Blocks.LAVA)) {
                            d2 = 1.0D;
                        } else if (d3 > -1.0D) {
                            MutableDouble mutabledouble = new MutableDouble(Double.NaN);
                            double d6 = this.calculatePressure(i, j, k, mutabledouble, aquifer_b1, aquifer_b2);
                            double d7 = this.calculatePressure(i, j, k, mutabledouble, aquifer_b1, aquifer_b3);
                            double d8 = this.calculatePressure(i, j, k, mutabledouble, aquifer_b2, aquifer_b3);
                            double d9 = Math.max(0.0D, d3);
                            double d10 = Math.max(0.0D, d4);
                            double d11 = Math.max(0.0D, d5);
                            double d12 = 2.0D * d9 * Math.max(d6, Math.max(d7 * d10, d8 * d11));

                            d2 = Math.max(0.0D, d12);
                        } else {
                            d2 = 0.0D;
                        }

                        iblockdata = aquifer_b1.at(j);
                    }

                    if (d1 + d2 <= 0.0D) {
                        this.shouldScheduleFluidUpdate = flag;
                        return iblockdata;
                    }
                }

                this.shouldScheduleFluidUpdate = false;
                return null;
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

        private double calculatePressure(int i, int j, int k, MutableDouble mutabledouble, Aquifer.b aquifer_b, Aquifer.b aquifer_b1) {
            IBlockData iblockdata = aquifer_b.at(j);
            IBlockData iblockdata1 = aquifer_b1.at(j);

            if ((!iblockdata.is(Blocks.LAVA) || !iblockdata1.is(Blocks.WATER)) && (!iblockdata.is(Blocks.WATER) || !iblockdata1.is(Blocks.LAVA))) {
                int l = Math.abs(aquifer_b.fluidLevel - aquifer_b1.fluidLevel);

                if (l == 0) {
                    return 0.0D;
                } else {
                    double d0 = 0.5D * (double) (aquifer_b.fluidLevel + aquifer_b1.fluidLevel);
                    double d1 = (double) j + 0.5D - d0;
                    double d2 = (double) l / 2.0D;
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

                    if (d11 >= -2.0D && d11 <= 2.0D) {
                        d10 = mutabledouble.getValue();
                        if (Double.isNaN(d10)) {
                            double d12 = 0.5D;
                            double d13 = this.barrierNoise.getValue((double) i, (double) j * 0.5D, (double) k);

                            mutabledouble.setValue(d13);
                            return d13 + d11;
                        } else {
                            return d10 + d11;
                        }
                    } else {
                        return d11;
                    }
                }
            } else {
                return 1.0D;
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

        @Override
        public Aquifer.b computeFluid(int i, int j, int k) {
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

            int i3 = l + 8 - j;
            boolean flag3 = true;
            double d0 = flag ? MathHelper.clampedMap((double) i3, 0.0D, 64.0D, 1.0D, 0.0D) : 0.0D;
            double d1 = 0.67D;
            double d2 = MathHelper.clamp(this.fluidLevelFloodednessNoise.getValue((double) i, (double) j * 0.67D, (double) k), -1.0D, 1.0D);
            double d3 = MathHelper.map(d0, 1.0D, 0.0D, -0.3D, 0.8D);

            if (d2 > d3) {
                return aquifer_b;
            } else {
                double d4 = MathHelper.map(d0, 1.0D, 0.0D, -0.8D, 0.4D);

                if (d2 <= d4) {
                    return new Aquifer.b(DimensionManager.WAY_BELOW_MIN_Y, aquifer_b.fluidType);
                } else {
                    boolean flag4 = true;
                    boolean flag5 = true;
                    int j3 = Math.floorDiv(i, 16);
                    int k3 = Math.floorDiv(j, 40);
                    int l3 = Math.floorDiv(k, 16);
                    int i4 = k3 * 40 + 20;
                    boolean flag6 = true;
                    double d5 = this.fluidLevelSpreadNoise.getValue((double) j3, (double) k3 / 1.4D, (double) l3) * 10.0D;
                    int j4 = MathHelper.quantize(d5, 3);
                    int k4 = i4 + j4;
                    int l4 = Math.min(l, k4);
                    IBlockData iblockdata = this.getFluidType(i, j, k, aquifer_b, k4);

                    return new Aquifer.b(l4, iblockdata);
                }
            }
        }

        private IBlockData getFluidType(int i, int j, int k, Aquifer.b aquifer_b, int l) {
            if (l <= -10) {
                boolean flag = true;
                boolean flag1 = true;
                int i1 = Math.floorDiv(i, 64);
                int j1 = Math.floorDiv(j, 40);
                int k1 = Math.floorDiv(k, 64);
                double d0 = this.lavaNoise.getValue((double) i1, (double) j1, (double) k1);

                if (Math.abs(d0) > 0.3D) {
                    return Blocks.LAVA.defaultBlockState();
                }
            }

            return aquifer_b.fluidType;
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

package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.BlockPosition2D;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;

public class NoiseChunk implements DensityFunction.a, DensityFunction.b {

    private final NoiseSettings noiseSettings;
    final int cellCountXZ;
    final int cellCountY;
    final int cellNoiseMinY;
    private final int firstCellX;
    private final int firstCellZ;
    final int firstNoiseX;
    final int firstNoiseZ;
    final List<NoiseChunk.i> interpolators;
    final List<NoiseChunk.e> cellCaches;
    private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();
    private final Long2IntMap preliminarySurfaceLevel = new Long2IntOpenHashMap();
    private final Aquifer aquifer;
    private final DensityFunction initialDensityNoJaggedness;
    private final NoiseChunk.c blockStateRule;
    private final Blender blender;
    private final NoiseChunk.g blendAlpha;
    private final NoiseChunk.g blendOffset;
    private final DensityFunctions.c beardifier;
    private long lastBlendingDataPos;
    private Blender.a lastBlendingOutput;
    final int noiseSizeXZ;
    final int cellWidth;
    final int cellHeight;
    boolean interpolating;
    boolean fillingCell;
    private int cellStartBlockX;
    int cellStartBlockY;
    private int cellStartBlockZ;
    int inCellX;
    int inCellY;
    int inCellZ;
    long interpolationCounter;
    long arrayInterpolationCounter;
    int arrayIndex;
    private final DensityFunction.a sliceFillingContextProvider;

    public static NoiseChunk forChunk(IChunkAccess ichunkaccess, RandomState randomstate, DensityFunctions.c densityfunctions_c, GeneratorSettingBase generatorsettingbase, Aquifer.a aquifer_a, Blender blender) {
        NoiseSettings noisesettings = generatorsettingbase.noiseSettings().clampToHeightAccessor(ichunkaccess);
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = 16 / noisesettings.getCellWidth();

        return new NoiseChunk(i, randomstate, chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMinBlockZ(), noisesettings, densityfunctions_c, generatorsettingbase, aquifer_a, blender);
    }

    public NoiseChunk(int i, RandomState randomstate, int j, int k, NoiseSettings noisesettings, DensityFunctions.c densityfunctions_c, GeneratorSettingBase generatorsettingbase, Aquifer.a aquifer_a, Blender blender) {
        this.lastBlendingDataPos = ChunkCoordIntPair.INVALID_CHUNK_POS;
        this.lastBlendingOutput = new Blender.a(1.0D, 0.0D);
        this.sliceFillingContextProvider = new DensityFunction.a() {
            @Override
            public DensityFunction.b forIndex(int l) {
                NoiseChunk.this.cellStartBlockY = (l + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
                ++NoiseChunk.this.interpolationCounter;
                NoiseChunk.this.inCellY = 0;
                NoiseChunk.this.arrayIndex = l;
                return NoiseChunk.this;
            }

            @Override
            public void fillAllDirectly(double[] adouble, DensityFunction densityfunction) {
                for (int l = 0; l < NoiseChunk.this.cellCountY + 1; ++l) {
                    NoiseChunk.this.cellStartBlockY = (l + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
                    ++NoiseChunk.this.interpolationCounter;
                    NoiseChunk.this.inCellY = 0;
                    NoiseChunk.this.arrayIndex = l;
                    adouble[l] = densityfunction.compute(NoiseChunk.this);
                }

            }
        };
        this.noiseSettings = noisesettings;
        this.cellWidth = noisesettings.getCellWidth();
        this.cellHeight = noisesettings.getCellHeight();
        this.cellCountXZ = i;
        this.cellCountY = MathHelper.floorDiv(noisesettings.height(), this.cellHeight);
        this.cellNoiseMinY = MathHelper.floorDiv(noisesettings.minY(), this.cellHeight);
        this.firstCellX = Math.floorDiv(j, this.cellWidth);
        this.firstCellZ = Math.floorDiv(k, this.cellWidth);
        this.interpolators = Lists.newArrayList();
        this.cellCaches = Lists.newArrayList();
        this.firstNoiseX = QuartPos.fromBlock(j);
        this.firstNoiseZ = QuartPos.fromBlock(k);
        this.noiseSizeXZ = QuartPos.fromBlock(i * this.cellWidth);
        this.blender = blender;
        this.beardifier = densityfunctions_c;
        this.blendAlpha = new NoiseChunk.g(new NoiseChunk.a(), false);
        this.blendOffset = new NoiseChunk.g(new NoiseChunk.b(), false);

        int l;
        int i1;

        for (int j1 = 0; j1 <= this.noiseSizeXZ; ++j1) {
            int k1 = this.firstNoiseX + j1;

            l = QuartPos.toBlock(k1);

            for (i1 = 0; i1 <= this.noiseSizeXZ; ++i1) {
                int l1 = this.firstNoiseZ + i1;
                int i2 = QuartPos.toBlock(l1);
                Blender.a blender_a = blender.blendOffsetAndFactor(l, i2);

                this.blendAlpha.values[j1][i1] = blender_a.alpha();
                this.blendOffset.values[j1][i1] = blender_a.blendingOffset();
            }
        }

        NoiseRouter noiserouter = randomstate.router();
        NoiseRouter noiserouter1 = noiserouter.mapAll(this::wrap);

        if (!generatorsettingbase.isAquifersEnabled()) {
            this.aquifer = Aquifer.createDisabled(aquifer_a);
        } else {
            l = SectionPosition.blockToSectionCoord(j);
            i1 = SectionPosition.blockToSectionCoord(k);
            this.aquifer = Aquifer.create(this, new ChunkCoordIntPair(l, i1), noiserouter1, randomstate.aquiferRandom(), noisesettings.minY(), noisesettings.height(), aquifer_a);
        }

        Builder<NoiseChunk.c> builder = ImmutableList.builder();
        DensityFunction densityfunction = DensityFunctions.cacheAllInCell(DensityFunctions.add(noiserouter1.finalDensity(), DensityFunctions.b.INSTANCE)).mapAll(this::wrap);

        builder.add((densityfunction_b) -> {
            return this.aquifer.computeSubstance(densityfunction_b, densityfunction.compute(densityfunction_b));
        });
        if (generatorsettingbase.oreVeinsEnabled()) {
            builder.add(OreVeinifier.create(noiserouter1.veinToggle(), noiserouter1.veinRidged(), noiserouter1.veinGap(), randomstate.oreRandom()));
        }

        this.blockStateRule = new MaterialRuleList(builder.build());
        this.initialDensityNoJaggedness = noiserouter1.initialDensityWithoutJaggedness();
    }

    protected Climate.Sampler cachedClimateSampler(NoiseRouter noiserouter, List<Climate.d> list) {
        return new Climate.Sampler(noiserouter.temperature().mapAll(this::wrap), noiserouter.vegetation().mapAll(this::wrap), noiserouter.continents().mapAll(this::wrap), noiserouter.erosion().mapAll(this::wrap), noiserouter.depth().mapAll(this::wrap), noiserouter.ridges().mapAll(this::wrap), list);
    }

    @Nullable
    protected IBlockData getInterpolatedState() {
        return this.blockStateRule.calculate(this);
    }

    @Override
    public int blockX() {
        return this.cellStartBlockX + this.inCellX;
    }

    @Override
    public int blockY() {
        return this.cellStartBlockY + this.inCellY;
    }

    @Override
    public int blockZ() {
        return this.cellStartBlockZ + this.inCellZ;
    }

    public int preliminarySurfaceLevel(int i, int j) {
        int k = QuartPos.toBlock(QuartPos.fromBlock(i));
        int l = QuartPos.toBlock(QuartPos.fromBlock(j));

        return this.preliminarySurfaceLevel.computeIfAbsent(BlockPosition2D.asLong(k, l), this::computePreliminarySurfaceLevel);
    }

    private int computePreliminarySurfaceLevel(long i) {
        int j = BlockPosition2D.getX(i);
        int k = BlockPosition2D.getZ(i);
        int l = this.noiseSettings.minY();

        for (int i1 = l + this.noiseSettings.height(); i1 >= l; i1 -= this.cellHeight) {
            if (this.initialDensityNoJaggedness.compute(new DensityFunction.e(j, i1, k)) > 0.390625D) {
                return i1;
            }
        }

        return Integer.MAX_VALUE;
    }

    @Override
    public Blender getBlender() {
        return this.blender;
    }

    private void fillSlice(boolean flag, int i) {
        this.cellStartBlockX = i * this.cellWidth;
        this.inCellX = 0;

        for (int j = 0; j < this.cellCountXZ + 1; ++j) {
            int k = this.firstCellZ + j;

            this.cellStartBlockZ = k * this.cellWidth;
            this.inCellZ = 0;
            ++this.arrayInterpolationCounter;
            Iterator iterator = this.interpolators.iterator();

            while (iterator.hasNext()) {
                NoiseChunk.i noisechunk_i = (NoiseChunk.i) iterator.next();
                double[] adouble = (flag ? noisechunk_i.slice0 : noisechunk_i.slice1)[j];

                noisechunk_i.fillArray(adouble, this.sliceFillingContextProvider);
            }
        }

        ++this.arrayInterpolationCounter;
    }

    public void initializeForFirstCellX() {
        if (this.interpolating) {
            throw new IllegalStateException("Staring interpolation twice");
        } else {
            this.interpolating = true;
            this.interpolationCounter = 0L;
            this.fillSlice(true, this.firstCellX);
        }
    }

    public void advanceCellX(int i) {
        this.fillSlice(false, this.firstCellX + i + 1);
        this.cellStartBlockX = (this.firstCellX + i) * this.cellWidth;
    }

    @Override
    public NoiseChunk forIndex(int i) {
        int j = Math.floorMod(i, this.cellWidth);
        int k = Math.floorDiv(i, this.cellWidth);
        int l = Math.floorMod(k, this.cellWidth);
        int i1 = this.cellHeight - 1 - Math.floorDiv(k, this.cellWidth);

        this.inCellX = l;
        this.inCellY = i1;
        this.inCellZ = j;
        this.arrayIndex = i;
        return this;
    }

    @Override
    public void fillAllDirectly(double[] adouble, DensityFunction densityfunction) {
        this.arrayIndex = 0;

        for (int i = this.cellHeight - 1; i >= 0; --i) {
            this.inCellY = i;

            for (int j = 0; j < this.cellWidth; ++j) {
                this.inCellX = j;

                for (int k = 0; k < this.cellWidth; ++k) {
                    this.inCellZ = k;
                    adouble[this.arrayIndex++] = densityfunction.compute(this);
                }
            }
        }

    }

    public void selectCellYZ(int i, int j) {
        this.interpolators.forEach((noisechunk_i) -> {
            noisechunk_i.selectCellYZ(i, j);
        });
        this.fillingCell = true;
        this.cellStartBlockY = (i + this.cellNoiseMinY) * this.cellHeight;
        this.cellStartBlockZ = (this.firstCellZ + j) * this.cellWidth;
        ++this.arrayInterpolationCounter;
        Iterator iterator = this.cellCaches.iterator();

        while (iterator.hasNext()) {
            NoiseChunk.e noisechunk_e = (NoiseChunk.e) iterator.next();

            noisechunk_e.noiseFiller.fillArray(noisechunk_e.values, this);
        }

        ++this.arrayInterpolationCounter;
        this.fillingCell = false;
    }

    public void updateForY(int i, double d0) {
        this.inCellY = i - this.cellStartBlockY;
        this.interpolators.forEach((noisechunk_i) -> {
            noisechunk_i.updateForY(d0);
        });
    }

    public void updateForX(int i, double d0) {
        this.inCellX = i - this.cellStartBlockX;
        this.interpolators.forEach((noisechunk_i) -> {
            noisechunk_i.updateForX(d0);
        });
    }

    public void updateForZ(int i, double d0) {
        this.inCellZ = i - this.cellStartBlockZ;
        ++this.interpolationCounter;
        this.interpolators.forEach((noisechunk_i) -> {
            noisechunk_i.updateForZ(d0);
        });
    }

    public void stopInterpolation() {
        if (!this.interpolating) {
            throw new IllegalStateException("Staring interpolation twice");
        } else {
            this.interpolating = false;
        }
    }

    public void swapSlices() {
        this.interpolators.forEach(NoiseChunk.i::swapSlices);
    }

    public Aquifer aquifer() {
        return this.aquifer;
    }

    protected int cellWidth() {
        return this.cellWidth;
    }

    protected int cellHeight() {
        return this.cellHeight;
    }

    Blender.a getOrComputeBlendingOutput(int i, int j) {
        long k = ChunkCoordIntPair.asLong(i, j);

        if (this.lastBlendingDataPos == k) {
            return this.lastBlendingOutput;
        } else {
            this.lastBlendingDataPos = k;
            Blender.a blender_a = this.blender.blendOffsetAndFactor(i, j);

            this.lastBlendingOutput = blender_a;
            return blender_a;
        }
    }

    protected DensityFunction wrap(DensityFunction densityfunction) {
        return (DensityFunction) this.wrapped.computeIfAbsent(densityfunction, this::wrapNew);
    }

    private DensityFunction wrapNew(DensityFunction densityfunction) {
        if (densityfunction instanceof DensityFunctions.l) {
            DensityFunctions.l densityfunctions_l = (DensityFunctions.l) densityfunction;
            Object object;

            switch (densityfunctions_l.type()) {
                case Interpolated:
                    object = new NoiseChunk.i(densityfunctions_l.wrapped());
                    break;
                case FlatCache:
                    object = new NoiseChunk.g(densityfunctions_l.wrapped(), true);
                    break;
                case Cache2D:
                    object = new NoiseChunk.d(densityfunctions_l.wrapped());
                    break;
                case CacheOnce:
                    object = new NoiseChunk.f(densityfunctions_l.wrapped());
                    break;
                case CacheAllInCell:
                    object = new NoiseChunk.e(densityfunctions_l.wrapped());
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return (DensityFunction) object;
        } else {
            if (this.blender != Blender.empty()) {
                if (densityfunction == DensityFunctions.d.INSTANCE) {
                    return this.blendAlpha;
                }

                if (densityfunction == DensityFunctions.f.INSTANCE) {
                    return this.blendOffset;
                }
            }

            if (densityfunction == DensityFunctions.b.INSTANCE) {
                return this.beardifier;
            } else if (densityfunction instanceof DensityFunctions.j) {
                DensityFunctions.j densityfunctions_j = (DensityFunctions.j) densityfunction;

                return (DensityFunction) densityfunctions_j.function().value();
            } else {
                return densityfunction;
            }
        }
    }

    private class g implements DensityFunctions.m, NoiseChunk.h {

        private final DensityFunction noiseFiller;
        final double[][] values;

        g(DensityFunction densityfunction, boolean flag) {
            this.noiseFiller = densityfunction;
            this.values = new double[NoiseChunk.this.noiseSizeXZ + 1][NoiseChunk.this.noiseSizeXZ + 1];
            if (flag) {
                for (int i = 0; i <= NoiseChunk.this.noiseSizeXZ; ++i) {
                    int j = NoiseChunk.this.firstNoiseX + i;
                    int k = QuartPos.toBlock(j);

                    for (int l = 0; l <= NoiseChunk.this.noiseSizeXZ; ++l) {
                        int i1 = NoiseChunk.this.firstNoiseZ + l;
                        int j1 = QuartPos.toBlock(i1);

                        this.values[i][l] = densityfunction.compute(new DensityFunction.e(k, 0, j1));
                    }
                }
            }

        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            int i = QuartPos.fromBlock(densityfunction_b.blockX());
            int j = QuartPos.fromBlock(densityfunction_b.blockZ());
            int k = i - NoiseChunk.this.firstNoiseX;
            int l = j - NoiseChunk.this.firstNoiseZ;
            int i1 = this.values.length;

            return k >= 0 && l >= 0 && k < i1 && l < i1 ? this.values[k][l] : this.noiseFiller.compute(densityfunction_b);
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        @Override
        public DensityFunctions.l.a type() {
            return DensityFunctions.l.a.FlatCache;
        }
    }

    private class a implements NoiseChunk.h {

        a() {}

        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.d.INSTANCE;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.f densityfunction_f) {
            return this.wrapped().mapAll(densityfunction_f);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return NoiseChunk.this.getOrComputeBlendingOutput(densityfunction_b.blockX(), densityfunction_b.blockZ()).alpha();
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        public double minValue() {
            return 0.0D;
        }

        @Override
        public double maxValue() {
            return 1.0D;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return DensityFunctions.d.CODEC;
        }
    }

    private class b implements NoiseChunk.h {

        b() {}

        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.f.INSTANCE;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.f densityfunction_f) {
            return this.wrapped().mapAll(densityfunction_f);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            return NoiseChunk.this.getOrComputeBlendingOutput(densityfunction_b.blockX(), densityfunction_b.blockZ()).blendingOffset();
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return DensityFunctions.f.CODEC;
        }
    }

    @FunctionalInterface
    public interface c {

        @Nullable
        IBlockData calculate(DensityFunction.b densityfunction_b);
    }

    public class i implements DensityFunctions.m, NoiseChunk.h {

        double[][] slice0;
        double[][] slice1;
        private final DensityFunction noiseFiller;
        private double noise000;
        private double noise001;
        private double noise100;
        private double noise101;
        private double noise010;
        private double noise011;
        private double noise110;
        private double noise111;
        private double valueXZ00;
        private double valueXZ10;
        private double valueXZ01;
        private double valueXZ11;
        private double valueZ0;
        private double valueZ1;
        private double value;

        i(DensityFunction densityfunction) {
            this.noiseFiller = densityfunction;
            this.slice0 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
            this.slice1 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
            NoiseChunk.this.interpolators.add(this);
        }

        private double[][] allocateSlice(int i, int j) {
            int k = j + 1;
            int l = i + 1;
            double[][] adouble = new double[k][l];

            for (int i1 = 0; i1 < k; ++i1) {
                adouble[i1] = new double[l];
            }

            return adouble;
        }

        void selectCellYZ(int i, int j) {
            this.noise000 = this.slice0[j][i];
            this.noise001 = this.slice0[j + 1][i];
            this.noise100 = this.slice1[j][i];
            this.noise101 = this.slice1[j + 1][i];
            this.noise010 = this.slice0[j][i + 1];
            this.noise011 = this.slice0[j + 1][i + 1];
            this.noise110 = this.slice1[j][i + 1];
            this.noise111 = this.slice1[j + 1][i + 1];
        }

        void updateForY(double d0) {
            this.valueXZ00 = MathHelper.lerp(d0, this.noise000, this.noise010);
            this.valueXZ10 = MathHelper.lerp(d0, this.noise100, this.noise110);
            this.valueXZ01 = MathHelper.lerp(d0, this.noise001, this.noise011);
            this.valueXZ11 = MathHelper.lerp(d0, this.noise101, this.noise111);
        }

        void updateForX(double d0) {
            this.valueZ0 = MathHelper.lerp(d0, this.valueXZ00, this.valueXZ10);
            this.valueZ1 = MathHelper.lerp(d0, this.valueXZ01, this.valueXZ11);
        }

        void updateForZ(double d0) {
            this.value = MathHelper.lerp(d0, this.valueZ0, this.valueZ1);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            if (densityfunction_b != NoiseChunk.this) {
                return this.noiseFiller.compute(densityfunction_b);
            } else if (!NoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            } else {
                return NoiseChunk.this.fillingCell ? MathHelper.lerp3((double) NoiseChunk.this.inCellX / (double) NoiseChunk.this.cellWidth, (double) NoiseChunk.this.inCellY / (double) NoiseChunk.this.cellHeight, (double) NoiseChunk.this.inCellZ / (double) NoiseChunk.this.cellWidth, this.noise000, this.noise100, this.noise010, this.noise110, this.noise001, this.noise101, this.noise011, this.noise111) : this.value;
            }
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            if (NoiseChunk.this.fillingCell) {
                densityfunction_a.fillAllDirectly(adouble, this);
            } else {
                this.wrapped().fillArray(adouble, densityfunction_a);
            }
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        private void swapSlices() {
            double[][] adouble = this.slice0;

            this.slice0 = this.slice1;
            this.slice1 = adouble;
        }

        @Override
        public DensityFunctions.l.a type() {
            return DensityFunctions.l.a.Interpolated;
        }
    }

    private class e implements DensityFunctions.m, NoiseChunk.h {

        final DensityFunction noiseFiller;
        final double[] values;

        e(DensityFunction densityfunction) {
            this.noiseFiller = densityfunction;
            this.values = new double[NoiseChunk.this.cellWidth * NoiseChunk.this.cellWidth * NoiseChunk.this.cellHeight];
            NoiseChunk.this.cellCaches.add(this);
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            if (densityfunction_b != NoiseChunk.this) {
                return this.noiseFiller.compute(densityfunction_b);
            } else if (!NoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            } else {
                int i = NoiseChunk.this.inCellX;
                int j = NoiseChunk.this.inCellY;
                int k = NoiseChunk.this.inCellZ;

                return i >= 0 && j >= 0 && k >= 0 && i < NoiseChunk.this.cellWidth && j < NoiseChunk.this.cellHeight && k < NoiseChunk.this.cellWidth ? this.values[((NoiseChunk.this.cellHeight - 1 - j) * NoiseChunk.this.cellWidth + i) * NoiseChunk.this.cellWidth + k] : this.noiseFiller.compute(densityfunction_b);
            }
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            densityfunction_a.fillAllDirectly(adouble, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        @Override
        public DensityFunctions.l.a type() {
            return DensityFunctions.l.a.CacheAllInCell;
        }
    }

    private static class d implements DensityFunctions.m, NoiseChunk.h {

        private final DensityFunction function;
        private long lastPos2D;
        private double lastValue;

        d(DensityFunction densityfunction) {
            this.lastPos2D = ChunkCoordIntPair.INVALID_CHUNK_POS;
            this.function = densityfunction;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            int i = densityfunction_b.blockX();
            int j = densityfunction_b.blockZ();
            long k = ChunkCoordIntPair.asLong(i, j);

            if (this.lastPos2D == k) {
                return this.lastValue;
            } else {
                this.lastPos2D = k;
                double d0 = this.function.compute(densityfunction_b);

                this.lastValue = d0;
                return d0;
            }
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            this.function.fillArray(adouble, densityfunction_a);
        }

        @Override
        public DensityFunction wrapped() {
            return this.function;
        }

        @Override
        public DensityFunctions.l.a type() {
            return DensityFunctions.l.a.Cache2D;
        }
    }

    private class f implements DensityFunctions.m, NoiseChunk.h {

        private final DensityFunction function;
        private long lastCounter;
        private long lastArrayCounter;
        private double lastValue;
        @Nullable
        private double[] lastArray;

        f(DensityFunction densityfunction) {
            this.function = densityfunction;
        }

        @Override
        public double compute(DensityFunction.b densityfunction_b) {
            if (densityfunction_b != NoiseChunk.this) {
                return this.function.compute(densityfunction_b);
            } else if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
                return this.lastArray[NoiseChunk.this.arrayIndex];
            } else if (this.lastCounter == NoiseChunk.this.interpolationCounter) {
                return this.lastValue;
            } else {
                this.lastCounter = NoiseChunk.this.interpolationCounter;
                double d0 = this.function.compute(densityfunction_b);

                this.lastValue = d0;
                return d0;
            }
        }

        @Override
        public void fillArray(double[] adouble, DensityFunction.a densityfunction_a) {
            if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
                System.arraycopy(this.lastArray, 0, adouble, 0, adouble.length);
            } else {
                this.wrapped().fillArray(adouble, densityfunction_a);
                if (this.lastArray != null && this.lastArray.length == adouble.length) {
                    System.arraycopy(adouble, 0, this.lastArray, 0, adouble.length);
                } else {
                    this.lastArray = (double[]) adouble.clone();
                }

                this.lastArrayCounter = NoiseChunk.this.arrayInterpolationCounter;
            }
        }

        @Override
        public DensityFunction wrapped() {
            return this.function;
        }

        @Override
        public DensityFunctions.l.a type() {
            return DensityFunctions.l.a.CacheOnce;
        }
    }

    private interface h extends DensityFunction {

        DensityFunction wrapped();

        @Override
        default double minValue() {
            return this.wrapped().minValue();
        }

        @Override
        default double maxValue() {
            return this.wrapped().maxValue();
        }
    }
}

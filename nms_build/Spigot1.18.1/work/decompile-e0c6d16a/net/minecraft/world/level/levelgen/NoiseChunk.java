package net.minecraft.world.level.levelgen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;

public class NoiseChunk {

    private final NoiseSampler sampler;
    final NoiseSettings noiseSettings;
    final int cellCountXZ;
    final int cellCountY;
    final int cellNoiseMinY;
    final int firstCellX;
    final int firstCellZ;
    private final int firstNoiseX;
    private final int firstNoiseZ;
    final List<NoiseChunk.d> interpolators;
    private final NoiseSampler.a[][] noiseData;
    private final Long2IntMap preliminarySurfaceLevel = new Long2IntOpenHashMap();
    private final Aquifer aquifer;
    private final NoiseChunk.a baseNoise;
    private final NoiseChunk.a oreVeins;
    private final Blender blender;

    public static NoiseChunk forChunk(IChunkAccess ichunkaccess, NoiseSampler noisesampler, Supplier<NoiseChunk.c> supplier, GeneratorSettingBase generatorsettingbase, Aquifer.a aquifer_a, Blender blender) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        NoiseSettings noisesettings = generatorsettingbase.noiseSettings();
        int i = Math.max(noisesettings.minY(), ichunkaccess.getMinBuildHeight());
        int j = Math.min(noisesettings.minY() + noisesettings.height(), ichunkaccess.getMaxBuildHeight());
        int k = MathHelper.intFloorDiv(i, noisesettings.getCellHeight());
        int l = MathHelper.intFloorDiv(j - i, noisesettings.getCellHeight());

        return new NoiseChunk(16 / noisesettings.getCellWidth(), l, k, noisesampler, chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMinBlockZ(), (NoiseChunk.c) supplier.get(), generatorsettingbase, aquifer_a, blender);
    }

    public static NoiseChunk forColumn(int i, int j, int k, int l, NoiseSampler noisesampler, GeneratorSettingBase generatorsettingbase, Aquifer.a aquifer_a) {
        return new NoiseChunk(1, l, k, noisesampler, i, j, (i1, j1, k1) -> {
            return 0.0D;
        }, generatorsettingbase, aquifer_a, Blender.empty());
    }

    private NoiseChunk(int i, int j, int k, NoiseSampler noisesampler, int l, int i1, NoiseChunk.c noisechunk_c, GeneratorSettingBase generatorsettingbase, Aquifer.a aquifer_a, Blender blender) {
        this.noiseSettings = generatorsettingbase.noiseSettings();
        this.cellCountXZ = i;
        this.cellCountY = j;
        this.cellNoiseMinY = k;
        this.sampler = noisesampler;
        int j1 = this.noiseSettings.getCellWidth();

        this.firstCellX = Math.floorDiv(l, j1);
        this.firstCellZ = Math.floorDiv(i1, j1);
        this.interpolators = Lists.newArrayList();
        this.firstNoiseX = QuartPos.fromBlock(l);
        this.firstNoiseZ = QuartPos.fromBlock(i1);
        int k1 = QuartPos.fromBlock(i * j1);

        this.noiseData = new NoiseSampler.a[k1 + 1][];
        this.blender = blender;

        for (int l1 = 0; l1 <= k1; ++l1) {
            int i2 = this.firstNoiseX + l1;

            this.noiseData[l1] = new NoiseSampler.a[k1 + 1];

            for (int j2 = 0; j2 <= k1; ++j2) {
                int k2 = this.firstNoiseZ + j2;

                this.noiseData[l1][j2] = noisesampler.noiseData(i2, k2, blender);
            }
        }

        this.aquifer = noisesampler.createAquifer(this, l, i1, k, j, aquifer_a, generatorsettingbase.isAquifersEnabled());
        this.baseNoise = noisesampler.makeBaseNoiseFiller(this, noisechunk_c, generatorsettingbase.isNoodleCavesEnabled());
        this.oreVeins = noisesampler.makeOreVeinifier(this, generatorsettingbase.isOreVeinsEnabled());
    }

    public NoiseSampler.a noiseData(int i, int j) {
        return this.noiseData[i - this.firstNoiseX][j - this.firstNoiseZ];
    }

    public int preliminarySurfaceLevel(int i, int j) {
        return this.preliminarySurfaceLevel.computeIfAbsent(ChunkCoordIntPair.asLong(QuartPos.fromBlock(i), QuartPos.fromBlock(j)), this::computePreliminarySurfaceLevel);
    }

    private int computePreliminarySurfaceLevel(long i) {
        int j = ChunkCoordIntPair.getX(i);
        int k = ChunkCoordIntPair.getZ(i);
        int l = j - this.firstNoiseX;
        int i1 = k - this.firstNoiseZ;
        int j1 = this.noiseData.length;
        TerrainInfo terraininfo;

        if (l >= 0 && i1 >= 0 && l < j1 && i1 < j1) {
            terraininfo = this.noiseData[l][i1].terrainInfo();
        } else {
            terraininfo = this.sampler.noiseData(j, k, this.blender).terrainInfo();
        }

        return this.sampler.getPreliminarySurfaceLevel(QuartPos.toBlock(j), QuartPos.toBlock(k), terraininfo);
    }

    protected NoiseChunk.d createNoiseInterpolator(NoiseChunk.c noisechunk_c) {
        return new NoiseChunk.d(noisechunk_c);
    }

    public Blender getBlender() {
        return this.blender;
    }

    public void initializeForFirstCellX() {
        this.interpolators.forEach((noisechunk_d) -> {
            noisechunk_d.initializeForFirstCellX();
        });
    }

    public void advanceCellX(int i) {
        this.interpolators.forEach((noisechunk_d) -> {
            noisechunk_d.advanceCellX(i);
        });
    }

    public void selectCellYZ(int i, int j) {
        this.interpolators.forEach((noisechunk_d) -> {
            noisechunk_d.selectCellYZ(i, j);
        });
    }

    public void updateForY(double d0) {
        this.interpolators.forEach((noisechunk_d) -> {
            noisechunk_d.updateForY(d0);
        });
    }

    public void updateForX(double d0) {
        this.interpolators.forEach((noisechunk_d) -> {
            noisechunk_d.updateForX(d0);
        });
    }

    public void updateForZ(double d0) {
        this.interpolators.forEach((noisechunk_d) -> {
            noisechunk_d.updateForZ(d0);
        });
    }

    public void swapSlices() {
        this.interpolators.forEach(NoiseChunk.d::swapSlices);
    }

    public Aquifer aquifer() {
        return this.aquifer;
    }

    @Nullable
    protected IBlockData updateNoiseAndGenerateBaseState(int i, int j, int k) {
        return this.baseNoise.calculate(i, j, k);
    }

    @Nullable
    protected IBlockData oreVeinify(int i, int j, int k) {
        return this.oreVeins.calculate(i, j, k);
    }

    @FunctionalInterface
    public interface c {

        double calculateNoise(int i, int j, int k);
    }

    @FunctionalInterface
    public interface a {

        @Nullable
        IBlockData calculate(int i, int j, int k);
    }

    public class d implements NoiseChunk.e {

        private double[][] slice0;
        private double[][] slice1;
        private final NoiseChunk.c noiseFiller;
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

        d(NoiseChunk.c noisechunk_c) {
            this.noiseFiller = noisechunk_c;
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

        void initializeForFirstCellX() {
            this.fillSlice(this.slice0, NoiseChunk.this.firstCellX);
        }

        void advanceCellX(int i) {
            this.fillSlice(this.slice1, NoiseChunk.this.firstCellX + i + 1);
        }

        private void fillSlice(double[][] adouble, int i) {
            int j = NoiseChunk.this.noiseSettings.getCellWidth();
            int k = NoiseChunk.this.noiseSettings.getCellHeight();

            for (int l = 0; l < NoiseChunk.this.cellCountXZ + 1; ++l) {
                int i1 = NoiseChunk.this.firstCellZ + l;

                for (int j1 = 0; j1 < NoiseChunk.this.cellCountY + 1; ++j1) {
                    int k1 = j1 + NoiseChunk.this.cellNoiseMinY;
                    int l1 = k1 * k;
                    double d0 = this.noiseFiller.calculateNoise(i * j, l1, i1 * j);

                    adouble[l][j1] = d0;
                }
            }

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
        public double sample() {
            return this.value;
        }

        private void swapSlices() {
            double[][] adouble = this.slice0;

            this.slice0 = this.slice1;
            this.slice1 = adouble;
        }
    }

    @FunctionalInterface
    public interface e {

        double sample();
    }

    @FunctionalInterface
    public interface b {

        NoiseChunk.e instantiate(NoiseChunk noisechunk);
    }
}

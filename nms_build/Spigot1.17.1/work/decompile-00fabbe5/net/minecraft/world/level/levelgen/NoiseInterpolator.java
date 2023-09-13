package net.minecraft.world.level.levelgen;

import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;

public class NoiseInterpolator {

    private double[][] slice0;
    private double[][] slice1;
    private final int cellCountY;
    private final int cellCountZ;
    private final int cellNoiseMinY;
    private final NoiseInterpolator.a noiseColumnFiller;
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
    private final int firstCellXInChunk;
    private final int firstCellZInChunk;

    public NoiseInterpolator(int i, int j, int k, ChunkCoordIntPair chunkcoordintpair, int l, NoiseInterpolator.a noiseinterpolator_a) {
        this.cellCountY = j;
        this.cellCountZ = k;
        this.cellNoiseMinY = l;
        this.noiseColumnFiller = noiseinterpolator_a;
        this.slice0 = b(j, k);
        this.slice1 = b(j, k);
        this.firstCellXInChunk = chunkcoordintpair.x * i;
        this.firstCellZInChunk = chunkcoordintpair.z * k;
    }

    private static double[][] b(int i, int j) {
        int k = j + 1;
        int l = i + 1;
        double[][] adouble = new double[k][l];

        for (int i1 = 0; i1 < k; ++i1) {
            adouble[i1] = new double[l];
        }

        return adouble;
    }

    public void a() {
        this.a(this.slice0, this.firstCellXInChunk);
    }

    public void a(int i) {
        this.a(this.slice1, this.firstCellXInChunk + i + 1);
    }

    private void a(double[][] adouble, int i) {
        for (int j = 0; j < this.cellCountZ + 1; ++j) {
            int k = this.firstCellZInChunk + j;

            this.noiseColumnFiller.fillNoiseColumn(adouble[j], i, k, this.cellNoiseMinY, this.cellCountY);
        }

    }

    public void a(int i, int j) {
        this.noise000 = this.slice0[j][i];
        this.noise001 = this.slice0[j + 1][i];
        this.noise100 = this.slice1[j][i];
        this.noise101 = this.slice1[j + 1][i];
        this.noise010 = this.slice0[j][i + 1];
        this.noise011 = this.slice0[j + 1][i + 1];
        this.noise110 = this.slice1[j][i + 1];
        this.noise111 = this.slice1[j + 1][i + 1];
    }

    public void a(double d0) {
        this.valueXZ00 = MathHelper.d(d0, this.noise000, this.noise010);
        this.valueXZ10 = MathHelper.d(d0, this.noise100, this.noise110);
        this.valueXZ01 = MathHelper.d(d0, this.noise001, this.noise011);
        this.valueXZ11 = MathHelper.d(d0, this.noise101, this.noise111);
    }

    public void b(double d0) {
        this.valueZ0 = MathHelper.d(d0, this.valueXZ00, this.valueXZ10);
        this.valueZ1 = MathHelper.d(d0, this.valueXZ01, this.valueXZ11);
    }

    public double c(double d0) {
        return MathHelper.d(d0, this.valueZ0, this.valueZ1);
    }

    public void b() {
        double[][] adouble = this.slice0;

        this.slice0 = this.slice1;
        this.slice1 = adouble;
    }

    @FunctionalInterface
    public interface a {

        void fillNoiseColumn(double[] adouble, int i, int j, int k, int l);
    }
}

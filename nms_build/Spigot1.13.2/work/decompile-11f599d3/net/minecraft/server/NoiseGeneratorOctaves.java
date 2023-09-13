package net.minecraft.server;

import java.util.Random;

public class NoiseGeneratorOctaves extends NoiseGenerator {

    private final NoiseGeneratorPerlin[] a;
    private final int b;

    public NoiseGeneratorOctaves(Random random, int i) {
        this.b = i;
        this.a = new NoiseGeneratorPerlin[i];

        for (int j = 0; j < i; ++j) {
            this.a[j] = new NoiseGeneratorPerlin(random);
        }

    }

    public double a(double d0, double d1, double d2) {
        double d3 = 0.0D;
        double d4 = 1.0D;

        for (int i = 0; i < this.b; ++i) {
            d3 += this.a[i].c(d0 * d4, d1 * d4, d2 * d4) / d4;
            d4 /= 2.0D;
        }

        return d3;
    }

    public double[] a(int i, int j, int k, int l, int i1, int j1, double d0, double d1, double d2) {
        double[] adouble = new double[l * i1 * j1];
        double d3 = 1.0D;

        for (int k1 = 0; k1 < this.b; ++k1) {
            double d4 = (double) i * d3 * d0;
            double d5 = (double) j * d3 * d1;
            double d6 = (double) k * d3 * d2;
            long l1 = MathHelper.d(d4);
            long i2 = MathHelper.d(d6);

            d4 -= (double) l1;
            d6 -= (double) i2;
            l1 %= 16777216L;
            i2 %= 16777216L;
            d4 += (double) l1;
            d6 += (double) i2;
            this.a[k1].a(adouble, d4, d5, d6, l, i1, j1, d0 * d3, d1 * d3, d2 * d3, d3);
            d3 /= 2.0D;
        }

        return adouble;
    }

    public double[] a(int i, int j, int k, int l, double d0, double d1, double d2) {
        return this.a(i, 10, j, k, 1, l, d0, 1.0D, d1);
    }
}

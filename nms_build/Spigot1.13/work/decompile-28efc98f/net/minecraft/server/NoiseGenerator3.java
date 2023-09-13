package net.minecraft.server;

import java.util.Random;

public class NoiseGenerator3 extends NoiseGenerator {

    private final NoiseGenerator3Handler[] a;
    private final int b;

    public NoiseGenerator3(Random random, int i) {
        this.b = i;
        this.a = new NoiseGenerator3Handler[i];

        for (int j = 0; j < i; ++j) {
            this.a[j] = new NoiseGenerator3Handler(random);
        }

    }

    public double a(double d0, double d1) {
        double d2 = 0.0D;
        double d3 = 1.0D;

        for (int i = 0; i < this.b; ++i) {
            d2 += this.a[i].a(d0 * d3, d1 * d3) / d3;
            d3 /= 2.0D;
        }

        return d2;
    }

    public double[] a(double d0, double d1, int i, int j, double d2, double d3, double d4) {
        return this.a(d0, d1, i, j, d2, d3, d4, 0.5D);
    }

    public double[] a(double d0, double d1, int i, int j, double d2, double d3, double d4, double d5) {
        double[] adouble = new double[i * j];
        double d6 = 1.0D;
        double d7 = 1.0D;

        for (int k = 0; k < this.b; ++k) {
            this.a[k].a(adouble, d0, d1, i, j, d2 * d7 * d6, d3 * d7 * d6, 0.55D / d6);
            d7 *= d4;
            d6 *= d5;
        }

        return adouble;
    }
}

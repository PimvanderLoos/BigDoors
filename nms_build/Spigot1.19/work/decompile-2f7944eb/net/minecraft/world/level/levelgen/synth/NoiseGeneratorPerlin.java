package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public final class NoiseGeneratorPerlin {

    private static final float SHIFT_UP_EPSILON = 1.0E-7F;
    private final byte[] p;
    public final double xo;
    public final double yo;
    public final double zo;

    public NoiseGeneratorPerlin(RandomSource randomsource) {
        this.xo = randomsource.nextDouble() * 256.0D;
        this.yo = randomsource.nextDouble() * 256.0D;
        this.zo = randomsource.nextDouble() * 256.0D;
        this.p = new byte[256];

        int i;

        for (i = 0; i < 256; ++i) {
            this.p[i] = (byte) i;
        }

        for (i = 0; i < 256; ++i) {
            int j = randomsource.nextInt(256 - i);
            byte b0 = this.p[i];

            this.p[i] = this.p[i + j];
            this.p[i + j] = b0;
        }

    }

    public double noise(double d0, double d1, double d2) {
        return this.noise(d0, d1, d2, 0.0D, 0.0D);
    }

    /** @deprecated */
    @Deprecated
    public double noise(double d0, double d1, double d2, double d3, double d4) {
        double d5 = d0 + this.xo;
        double d6 = d1 + this.yo;
        double d7 = d2 + this.zo;
        int i = MathHelper.floor(d5);
        int j = MathHelper.floor(d6);
        int k = MathHelper.floor(d7);
        double d8 = d5 - (double) i;
        double d9 = d6 - (double) j;
        double d10 = d7 - (double) k;
        double d11;

        if (d3 != 0.0D) {
            double d12;

            if (d4 >= 0.0D && d4 < d9) {
                d12 = d4;
            } else {
                d12 = d9;
            }

            d11 = (double) MathHelper.floor(d12 / d3 + 1.0000000116860974E-7D) * d3;
        } else {
            d11 = 0.0D;
        }

        return this.sampleAndLerp(i, j, k, d8, d9 - d11, d10, d9);
    }

    public double noiseWithDerivative(double d0, double d1, double d2, double[] adouble) {
        double d3 = d0 + this.xo;
        double d4 = d1 + this.yo;
        double d5 = d2 + this.zo;
        int i = MathHelper.floor(d3);
        int j = MathHelper.floor(d4);
        int k = MathHelper.floor(d5);
        double d6 = d3 - (double) i;
        double d7 = d4 - (double) j;
        double d8 = d5 - (double) k;

        return this.sampleWithDerivative(i, j, k, d6, d7, d8, adouble);
    }

    private static double gradDot(int i, double d0, double d1, double d2) {
        return NoiseGenerator3Handler.dot(NoiseGenerator3Handler.GRADIENT[i & 15], d0, d1, d2);
    }

    private int p(int i) {
        return this.p[i & 255] & 255;
    }

    private double sampleAndLerp(int i, int j, int k, double d0, double d1, double d2, double d3) {
        int l = this.p(i);
        int i1 = this.p(i + 1);
        int j1 = this.p(l + j);
        int k1 = this.p(l + j + 1);
        int l1 = this.p(i1 + j);
        int i2 = this.p(i1 + j + 1);
        double d4 = gradDot(this.p(j1 + k), d0, d1, d2);
        double d5 = gradDot(this.p(l1 + k), d0 - 1.0D, d1, d2);
        double d6 = gradDot(this.p(k1 + k), d0, d1 - 1.0D, d2);
        double d7 = gradDot(this.p(i2 + k), d0 - 1.0D, d1 - 1.0D, d2);
        double d8 = gradDot(this.p(j1 + k + 1), d0, d1, d2 - 1.0D);
        double d9 = gradDot(this.p(l1 + k + 1), d0 - 1.0D, d1, d2 - 1.0D);
        double d10 = gradDot(this.p(k1 + k + 1), d0, d1 - 1.0D, d2 - 1.0D);
        double d11 = gradDot(this.p(i2 + k + 1), d0 - 1.0D, d1 - 1.0D, d2 - 1.0D);
        double d12 = MathHelper.smoothstep(d0);
        double d13 = MathHelper.smoothstep(d3);
        double d14 = MathHelper.smoothstep(d2);

        return MathHelper.lerp3(d12, d13, d14, d4, d5, d6, d7, d8, d9, d10, d11);
    }

    private double sampleWithDerivative(int i, int j, int k, double d0, double d1, double d2, double[] adouble) {
        int l = this.p(i);
        int i1 = this.p(i + 1);
        int j1 = this.p(l + j);
        int k1 = this.p(l + j + 1);
        int l1 = this.p(i1 + j);
        int i2 = this.p(i1 + j + 1);
        int j2 = this.p(j1 + k);
        int k2 = this.p(l1 + k);
        int l2 = this.p(k1 + k);
        int i3 = this.p(i2 + k);
        int j3 = this.p(j1 + k + 1);
        int k3 = this.p(l1 + k + 1);
        int l3 = this.p(k1 + k + 1);
        int i4 = this.p(i2 + k + 1);
        int[] aint = NoiseGenerator3Handler.GRADIENT[j2 & 15];
        int[] aint1 = NoiseGenerator3Handler.GRADIENT[k2 & 15];
        int[] aint2 = NoiseGenerator3Handler.GRADIENT[l2 & 15];
        int[] aint3 = NoiseGenerator3Handler.GRADIENT[i3 & 15];
        int[] aint4 = NoiseGenerator3Handler.GRADIENT[j3 & 15];
        int[] aint5 = NoiseGenerator3Handler.GRADIENT[k3 & 15];
        int[] aint6 = NoiseGenerator3Handler.GRADIENT[l3 & 15];
        int[] aint7 = NoiseGenerator3Handler.GRADIENT[i4 & 15];
        double d3 = NoiseGenerator3Handler.dot(aint, d0, d1, d2);
        double d4 = NoiseGenerator3Handler.dot(aint1, d0 - 1.0D, d1, d2);
        double d5 = NoiseGenerator3Handler.dot(aint2, d0, d1 - 1.0D, d2);
        double d6 = NoiseGenerator3Handler.dot(aint3, d0 - 1.0D, d1 - 1.0D, d2);
        double d7 = NoiseGenerator3Handler.dot(aint4, d0, d1, d2 - 1.0D);
        double d8 = NoiseGenerator3Handler.dot(aint5, d0 - 1.0D, d1, d2 - 1.0D);
        double d9 = NoiseGenerator3Handler.dot(aint6, d0, d1 - 1.0D, d2 - 1.0D);
        double d10 = NoiseGenerator3Handler.dot(aint7, d0 - 1.0D, d1 - 1.0D, d2 - 1.0D);
        double d11 = MathHelper.smoothstep(d0);
        double d12 = MathHelper.smoothstep(d1);
        double d13 = MathHelper.smoothstep(d2);
        double d14 = MathHelper.lerp3(d11, d12, d13, (double) aint[0], (double) aint1[0], (double) aint2[0], (double) aint3[0], (double) aint4[0], (double) aint5[0], (double) aint6[0], (double) aint7[0]);
        double d15 = MathHelper.lerp3(d11, d12, d13, (double) aint[1], (double) aint1[1], (double) aint2[1], (double) aint3[1], (double) aint4[1], (double) aint5[1], (double) aint6[1], (double) aint7[1]);
        double d16 = MathHelper.lerp3(d11, d12, d13, (double) aint[2], (double) aint1[2], (double) aint2[2], (double) aint3[2], (double) aint4[2], (double) aint5[2], (double) aint6[2], (double) aint7[2]);
        double d17 = MathHelper.lerp2(d12, d13, d4 - d3, d6 - d5, d8 - d7, d10 - d9);
        double d18 = MathHelper.lerp2(d13, d11, d5 - d3, d9 - d7, d6 - d4, d10 - d8);
        double d19 = MathHelper.lerp2(d11, d12, d7 - d3, d8 - d4, d9 - d5, d10 - d6);
        double d20 = MathHelper.smoothstepDerivative(d0);
        double d21 = MathHelper.smoothstepDerivative(d1);
        double d22 = MathHelper.smoothstepDerivative(d2);
        double d23 = d14 + d20 * d17;
        double d24 = d15 + d21 * d18;
        double d25 = d16 + d22 * d19;

        adouble[0] += d23;
        adouble[1] += d24;
        adouble[2] += d25;
        return MathHelper.lerp3(d11, d12, d13, d3, d4, d5, d6, d7, d8, d9, d10);
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringbuilder) {
        NoiseUtils.parityNoiseOctaveConfigString(stringbuilder, this.xo, this.yo, this.zo, this.p);
    }
}

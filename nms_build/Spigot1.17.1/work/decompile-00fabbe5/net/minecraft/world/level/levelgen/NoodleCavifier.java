package net.minecraft.world.level.levelgen;

import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;

public class NoodleCavifier {

    private static final int NOODLES_MAX_Y = 30;
    private static final double SPACING_AND_STRAIGHTNESS = 1.5D;
    private static final double XZ_FREQUENCY = 2.6666666666666665D;
    private static final double Y_FREQUENCY = 2.6666666666666665D;
    private final NoiseGeneratorNormal toggleNoiseSource;
    private final NoiseGeneratorNormal thicknessNoiseSource;
    private final NoiseGeneratorNormal noodleANoiseSource;
    private final NoiseGeneratorNormal noodleBNoiseSource;

    public NoodleCavifier(long i) {
        Random random = new Random(i);

        this.toggleNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -8, 1.0D);
        this.thicknessNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -8, 1.0D);
        this.noodleANoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
        this.noodleBNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
    }

    public void a(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, k, l, this.toggleNoiseSource, 1.0D);
    }

    public void b(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, k, l, this.thicknessNoiseSource, 1.0D);
    }

    public void c(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, k, l, this.noodleANoiseSource, 2.6666666666666665D, 2.6666666666666665D);
    }

    public void d(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, k, l, this.noodleBNoiseSource, 2.6666666666666665D, 2.6666666666666665D);
    }

    public void a(double[] adouble, int i, int j, int k, int l, NoiseGeneratorNormal noisegeneratornormal, double d0) {
        this.a(adouble, i, j, k, l, noisegeneratornormal, d0, d0);
    }

    public void a(double[] adouble, int i, int j, int k, int l, NoiseGeneratorNormal noisegeneratornormal, double d0, double d1) {
        boolean flag = true;
        boolean flag1 = true;

        for (int i1 = 0; i1 < l; ++i1) {
            int j1 = i1 + k;
            int k1 = i * 4;
            int l1 = j1 * 8;
            int i2 = j * 4;
            double d2;

            if (l1 < 38) {
                d2 = NoiseUtils.a(noisegeneratornormal, (double) k1 * d0, (double) l1 * d1, (double) i2 * d0, -1.0D, 1.0D);
            } else {
                d2 = 1.0D;
            }

            adouble[i1] = d2;
        }

    }

    public double a(double d0, int i, int j, int k, double d1, double d2, double d3, double d4, int l) {
        if (j <= 30 && j >= l + 4) {
            if (d0 < 0.0D) {
                return d0;
            } else if (d1 < 0.0D) {
                return d0;
            } else {
                double d5 = 0.05D;
                double d6 = 0.1D;
                double d7 = MathHelper.a(d2, -1.0D, 1.0D, 0.05D, 0.1D);
                double d8 = Math.abs(1.5D * d3) - d7;
                double d9 = Math.abs(1.5D * d4) - d7;
                double d10 = Math.max(d8, d9);

                return Math.min(d0, d10);
            }
        } else {
            return d0;
        }
    }
}

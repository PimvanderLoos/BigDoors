package net.minecraft.world.level.levelgen;

import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;

public class Cavifier implements NoiseModifier {

    private final int minCellY;
    private final NoiseGeneratorNormal layerNoiseSource;
    private final NoiseGeneratorNormal pillarNoiseSource;
    private final NoiseGeneratorNormal pillarRarenessModulator;
    private final NoiseGeneratorNormal pillarThicknessModulator;
    private final NoiseGeneratorNormal spaghetti2dNoiseSource;
    private final NoiseGeneratorNormal spaghetti2dElevationModulator;
    private final NoiseGeneratorNormal spaghetti2dRarityModulator;
    private final NoiseGeneratorNormal spaghetti2dThicknessModulator;
    private final NoiseGeneratorNormal spaghetti3dNoiseSource1;
    private final NoiseGeneratorNormal spaghetti3dNoiseSource2;
    private final NoiseGeneratorNormal spaghetti3dRarityModulator;
    private final NoiseGeneratorNormal spaghetti3dThicknessModulator;
    private final NoiseGeneratorNormal spaghettiRoughnessNoise;
    private final NoiseGeneratorNormal spaghettiRoughnessModulator;
    private final NoiseGeneratorNormal caveEntranceNoiseSource;
    private final NoiseGeneratorNormal cheeseNoiseSource;
    private static final int CHEESE_NOISE_RANGE = 128;
    private static final int SURFACE_DENSITY_THRESHOLD = 170;

    public Cavifier(RandomSource randomsource, int i) {
        this.minCellY = i;
        this.pillarNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -7, 1.0D, 1.0D);
        this.pillarRarenessModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D);
        this.pillarThicknessModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D);
        this.spaghetti2dNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -7, 1.0D);
        this.spaghetti2dElevationModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D);
        this.spaghetti2dRarityModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -11, 1.0D);
        this.spaghetti2dThicknessModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -11, 1.0D);
        this.spaghetti3dNoiseSource1 = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -7, 1.0D);
        this.spaghetti3dNoiseSource2 = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -7, 1.0D);
        this.spaghetti3dRarityModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -11, 1.0D);
        this.spaghetti3dThicknessModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D);
        this.spaghettiRoughnessNoise = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -5, 1.0D);
        this.spaghettiRoughnessModulator = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D);
        this.caveEntranceNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D, 1.0D, 1.0D);
        this.layerNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 1.0D);
        this.cheeseNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(randomsource.nextLong()), -8, 0.5D, 1.0D, 2.0D, 1.0D, 2.0D, 1.0D, 0.0D, 2.0D, 0.0D);
    }

    @Override
    public double modifyNoise(double d0, int i, int j, int k) {
        boolean flag = d0 < 170.0D;
        double d1 = this.e(k, i, j);
        double d2 = this.c(k, i, j);

        if (flag) {
            return Math.min(d0, (d2 + d1) * 128.0D * 5.0D);
        } else {
            double d3 = this.cheeseNoiseSource.a((double) k, (double) i / 1.5D, (double) j);
            double d4 = MathHelper.a(d3 + 0.25D, -1.0D, 1.0D);
            double d5 = (double) ((float) (30 - i) / 8.0F);
            double d6 = d4 + MathHelper.b(0.5D, 0.0D, d5);
            double d7 = this.b(k, i, j);
            double d8 = this.d(k, i, j);
            double d9 = d6 + d7;
            double d10 = Math.min(d9, Math.min(d2, d8) + d1);
            double d11 = Math.max(d10, this.a(k, i, j));

            return 128.0D * MathHelper.a(d11, -1.0D, 1.0D);
        }
    }

    private double a(double d0, int i, int j, int k) {
        double d1 = this.caveEntranceNoiseSource.a((double) (i * 2), (double) j, (double) (k * 2));

        d1 = NoiseUtils.a(d1, 1.0D);
        boolean flag = false;
        double d2 = (double) (j - 0) / 40.0D;

        d1 += MathHelper.b(0.5D, d0, d2);
        double d3 = 3.0D;

        d1 = 4.0D * d1 + 3.0D;
        return Math.min(d0, d1);
    }

    private double a(int i, int j, int k) {
        double d0 = 0.0D;
        double d1 = 2.0D;
        double d2 = NoiseUtils.a(this.pillarRarenessModulator, (double) i, (double) j, (double) k, 0.0D, 2.0D);
        double d3 = 0.0D;
        double d4 = 1.1D;
        double d5 = NoiseUtils.a(this.pillarThicknessModulator, (double) i, (double) j, (double) k, 0.0D, 1.1D);

        d5 = Math.pow(d5, 3.0D);
        double d6 = 25.0D;
        double d7 = 0.3D;
        double d8 = this.pillarNoiseSource.a((double) i * 25.0D, (double) j * 0.3D, (double) k * 25.0D);

        d8 = d5 * (d8 * 2.0D - d2);
        return d8 > 0.03D ? d8 : Double.NEGATIVE_INFINITY;
    }

    private double b(int i, int j, int k) {
        double d0 = this.layerNoiseSource.a((double) i, (double) (j * 8), (double) k);

        return MathHelper.m(d0) * 4.0D;
    }

    private double c(int i, int j, int k) {
        double d0 = this.spaghetti3dRarityModulator.a((double) (i * 2), (double) j, (double) (k * 2));
        double d1 = Cavifier.a.b(d0);
        double d2 = 0.065D;
        double d3 = 0.088D;
        double d4 = NoiseUtils.a(this.spaghetti3dThicknessModulator, (double) i, (double) j, (double) k, 0.065D, 0.088D);
        double d5 = a(this.spaghetti3dNoiseSource1, (double) i, (double) j, (double) k, d1);
        double d6 = Math.abs(d1 * d5) - d4;
        double d7 = a(this.spaghetti3dNoiseSource2, (double) i, (double) j, (double) k, d1);
        double d8 = Math.abs(d1 * d7) - d4;

        return a(Math.max(d6, d8));
    }

    private double d(int i, int j, int k) {
        double d0 = this.spaghetti2dRarityModulator.a((double) (i * 2), (double) j, (double) (k * 2));
        double d1 = Cavifier.a.a(d0);
        double d2 = 0.6D;
        double d3 = 1.3D;
        double d4 = NoiseUtils.a(this.spaghetti2dThicknessModulator, (double) (i * 2), (double) j, (double) (k * 2), 0.6D, 1.3D);
        double d5 = a(this.spaghetti2dNoiseSource, (double) i, (double) j, (double) k, d1);
        double d6 = 0.083D;
        double d7 = Math.abs(d1 * d5) - 0.083D * d4;
        int l = this.minCellY;
        boolean flag = true;
        double d8 = NoiseUtils.a(this.spaghetti2dElevationModulator, (double) i, 0.0D, (double) k, (double) l, 8.0D);
        double d9 = Math.abs(d8 - (double) j / 8.0D) - 1.0D * d4;

        d9 = d9 * d9 * d9;
        return a(Math.max(d9, d7));
    }

    private double e(int i, int j, int k) {
        double d0 = NoiseUtils.a(this.spaghettiRoughnessModulator, (double) i, (double) j, (double) k, 0.0D, 0.1D);

        return (0.4D - Math.abs(this.spaghettiRoughnessNoise.a((double) i, (double) j, (double) k))) * d0;
    }

    private static double a(double d0) {
        return MathHelper.a(d0, -1.0D, 1.0D);
    }

    private static double a(NoiseGeneratorNormal noisegeneratornormal, double d0, double d1, double d2, double d3) {
        return noisegeneratornormal.a(d0 / d3, d1 / d3, d2 / d3);
    }

    private static final class a {

        private a() {}

        static double a(double d0) {
            return d0 < -0.75D ? 0.5D : (d0 < -0.5D ? 0.75D : (d0 < 0.5D ? 1.0D : (d0 < 0.75D ? 2.0D : 3.0D)));
        }

        static double b(double d0) {
            return d0 < -0.5D ? 0.75D : (d0 < 0.0D ? 1.0D : (d0 < 0.5D ? 1.5D : 2.0D));
        }
    }
}

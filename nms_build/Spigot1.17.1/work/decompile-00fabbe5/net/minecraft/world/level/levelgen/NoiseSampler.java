package net.minecraft.world.level.levelgen;

import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3Handler;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctaves;

public class NoiseSampler {

    private static final int OLD_CELL_COUNT_Y = 32;
    private static final float[] BIOME_WEIGHTS = (float[]) SystemUtils.a((Object) (new float[25]), (afloat) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.c((float) (i * i + j * j) + 0.2F);

                afloat[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private final WorldChunkManager biomeSource;
    private final int cellWidth;
    private final int cellHeight;
    private final int cellCountY;
    private final NoiseSettings noiseSettings;
    private final BlendedNoise blendedNoise;
    @Nullable
    private final NoiseGenerator3Handler islandNoise;
    private final NoiseGeneratorOctaves depthNoise;
    private final double topSlideTarget;
    private final double topSlideSize;
    private final double topSlideOffset;
    private final double bottomSlideTarget;
    private final double bottomSlideSize;
    private final double bottomSlideOffset;
    private final double dimensionDensityFactor;
    private final double dimensionDensityOffset;
    private final NoiseModifier caveNoiseModifier;

    public NoiseSampler(WorldChunkManager worldchunkmanager, int i, int j, int k, NoiseSettings noisesettings, BlendedNoise blendednoise, @Nullable NoiseGenerator3Handler noisegenerator3handler, NoiseGeneratorOctaves noisegeneratoroctaves, NoiseModifier noisemodifier) {
        this.cellWidth = i;
        this.cellHeight = j;
        this.biomeSource = worldchunkmanager;
        this.cellCountY = k;
        this.noiseSettings = noisesettings;
        this.blendedNoise = blendednoise;
        this.islandNoise = noisegenerator3handler;
        this.depthNoise = noisegeneratoroctaves;
        this.topSlideTarget = (double) noisesettings.d().a();
        this.topSlideSize = (double) noisesettings.d().b();
        this.topSlideOffset = (double) noisesettings.d().c();
        this.bottomSlideTarget = (double) noisesettings.e().a();
        this.bottomSlideSize = (double) noisesettings.e().b();
        this.bottomSlideOffset = (double) noisesettings.e().c();
        this.dimensionDensityFactor = noisesettings.h();
        this.dimensionDensityOffset = noisesettings.i();
        this.caveNoiseModifier = noisemodifier;
    }

    public void a(double[] adouble, int i, int j, NoiseSettings noisesettings, int k, int l, int i1) {
        double d0;
        double d1;
        double d2;

        if (this.islandNoise != null) {
            d0 = (double) (WorldChunkManagerTheEnd.a(this.islandNoise, i, j) - 8.0F);
            if (d0 > 0.0D) {
                d1 = 0.25D;
            } else {
                d1 = 1.0D;
            }
        } else {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            boolean flag = true;
            int j1 = k;
            float f3 = this.biomeSource.getBiome(i, k, j).h();

            for (int k1 = -2; k1 <= 2; ++k1) {
                for (int l1 = -2; l1 <= 2; ++l1) {
                    BiomeBase biomebase = this.biomeSource.getBiome(i + k1, j1, j + l1);
                    float f4 = biomebase.h();
                    float f5 = biomebase.j();
                    float f6;
                    float f7;

                    if (noisesettings.m() && f4 > 0.0F) {
                        f6 = 1.0F + f4 * 2.0F;
                        f7 = 1.0F + f5 * 4.0F;
                    } else {
                        f6 = f4;
                        f7 = f5;
                    }

                    float f8 = f4 > f3 ? 0.5F : 1.0F;
                    float f9 = f8 * NoiseSampler.BIOME_WEIGHTS[k1 + 2 + (l1 + 2) * 5] / (f6 + 2.0F);

                    f += f7 * f9;
                    f1 += f6 * f9;
                    f2 += f9;
                }
            }

            float f10 = f1 / f2;
            float f11 = f / f2;

            d2 = (double) (f10 * 0.5F - 0.125F);
            double d3 = (double) (f11 * 0.9F + 0.1F);

            d0 = d2 * 0.265625D;
            d1 = 96.0D / d3;
        }

        double d4 = 684.412D * noisesettings.c().a();
        double d5 = 684.412D * noisesettings.c().b();
        double d6 = d4 / noisesettings.c().c();
        double d7 = d5 / noisesettings.c().d();

        d2 = noisesettings.k() ? this.a(i, j) : 0.0D;

        for (int i2 = 0; i2 <= i1; ++i2) {
            int j2 = i2 + l;
            double d8 = this.blendedNoise.a(i, j2, j, d4, d5, d6, d7);
            double d9 = this.a(j2, d0, d1, d2) + d8;

            d9 = this.caveNoiseModifier.modifyNoise(d9, j2 * this.cellHeight, j * this.cellWidth, i * this.cellWidth);
            d9 = this.a(d9, j2);
            adouble[i2] = d9;
        }

    }

    private double a(int i, double d0, double d1, double d2) {
        double d3 = 1.0D - (double) i * 2.0D / 32.0D + d2;
        double d4 = d3 * this.dimensionDensityFactor + this.dimensionDensityOffset;
        double d5 = (d4 + d0) * d1;

        return d5 * (double) (d5 > 0.0D ? 4 : 1);
    }

    private double a(double d0, int i) {
        int j = MathHelper.a(this.noiseSettings.a(), this.cellHeight);
        int k = i - j;
        double d1;

        if (this.topSlideSize > 0.0D) {
            d1 = ((double) (this.cellCountY - k) - this.topSlideOffset) / this.topSlideSize;
            d0 = MathHelper.b(this.topSlideTarget, d0, d1);
        }

        if (this.bottomSlideSize > 0.0D) {
            d1 = ((double) k - this.bottomSlideOffset) / this.bottomSlideSize;
            d0 = MathHelper.b(this.bottomSlideTarget, d0, d1);
        }

        return d0;
    }

    private double a(int i, int j) {
        double d0 = this.depthNoise.a((double) (i * 200), 10.0D, (double) (j * 200), 1.0D, 0.0D, true);
        double d1;

        if (d0 < 0.0D) {
            d1 = -d0 * 0.3D;
        } else {
            d1 = d0;
        }

        double d2 = d1 * 24.575625D - 2.0D;

        return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
    }
}

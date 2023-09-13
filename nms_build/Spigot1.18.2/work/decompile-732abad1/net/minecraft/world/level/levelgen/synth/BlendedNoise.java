package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class BlendedNoise implements DensityFunction.c {

    public static final BlendedNoise UNSEEDED = new BlendedNoise(new XoroshiroRandomSource(0L), new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), 4, 8);
    public static final Codec<BlendedNoise> CODEC = Codec.unit(BlendedNoise.UNSEEDED);
    private final NoiseGeneratorOctaves minLimitNoise;
    private final NoiseGeneratorOctaves maxLimitNoise;
    private final NoiseGeneratorOctaves mainNoise;
    private final double xzScale;
    private final double yScale;
    private final double xzMainScale;
    private final double yMainScale;
    private final int cellWidth;
    private final int cellHeight;
    private final double maxValue;

    private BlendedNoise(NoiseGeneratorOctaves noisegeneratoroctaves, NoiseGeneratorOctaves noisegeneratoroctaves1, NoiseGeneratorOctaves noisegeneratoroctaves2, NoiseSamplingSettings noisesamplingsettings, int i, int j) {
        this.minLimitNoise = noisegeneratoroctaves;
        this.maxLimitNoise = noisegeneratoroctaves1;
        this.mainNoise = noisegeneratoroctaves2;
        this.xzScale = 684.412D * noisesamplingsettings.xzScale();
        this.yScale = 684.412D * noisesamplingsettings.yScale();
        this.xzMainScale = this.xzScale / noisesamplingsettings.xzFactor();
        this.yMainScale = this.yScale / noisesamplingsettings.yFactor();
        this.cellWidth = i;
        this.cellHeight = j;
        this.maxValue = noisegeneratoroctaves.maxBrokenValue(this.yScale);
    }

    public BlendedNoise(RandomSource randomsource, NoiseSamplingSettings noisesamplingsettings, int i, int j) {
        this(NoiseGeneratorOctaves.createLegacyForBlendedNoise(randomsource, IntStream.rangeClosed(-15, 0)), NoiseGeneratorOctaves.createLegacyForBlendedNoise(randomsource, IntStream.rangeClosed(-15, 0)), NoiseGeneratorOctaves.createLegacyForBlendedNoise(randomsource, IntStream.rangeClosed(-7, 0)), noisesamplingsettings, i, j);
    }

    @Override
    public double compute(DensityFunction.b densityfunction_b) {
        int i = Math.floorDiv(densityfunction_b.blockX(), this.cellWidth);
        int j = Math.floorDiv(densityfunction_b.blockY(), this.cellHeight);
        int k = Math.floorDiv(densityfunction_b.blockZ(), this.cellWidth);
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        boolean flag = true;
        double d3 = 1.0D;

        for (int l = 0; l < 8; ++l) {
            NoiseGeneratorPerlin noisegeneratorperlin = this.mainNoise.getOctaveNoise(l);

            if (noisegeneratorperlin != null) {
                d2 += noisegeneratorperlin.noise(NoiseGeneratorOctaves.wrap((double) i * this.xzMainScale * d3), NoiseGeneratorOctaves.wrap((double) j * this.yMainScale * d3), NoiseGeneratorOctaves.wrap((double) k * this.xzMainScale * d3), this.yMainScale * d3, (double) j * this.yMainScale * d3) / d3;
            }

            d3 /= 2.0D;
        }

        double d4 = (d2 / 10.0D + 1.0D) / 2.0D;
        boolean flag1 = d4 >= 1.0D;
        boolean flag2 = d4 <= 0.0D;

        d3 = 1.0D;

        for (int i1 = 0; i1 < 16; ++i1) {
            double d5 = NoiseGeneratorOctaves.wrap((double) i * this.xzScale * d3);
            double d6 = NoiseGeneratorOctaves.wrap((double) j * this.yScale * d3);
            double d7 = NoiseGeneratorOctaves.wrap((double) k * this.xzScale * d3);
            double d8 = this.yScale * d3;
            NoiseGeneratorPerlin noisegeneratorperlin1;

            if (!flag1) {
                noisegeneratorperlin1 = this.minLimitNoise.getOctaveNoise(i1);
                if (noisegeneratorperlin1 != null) {
                    d0 += noisegeneratorperlin1.noise(d5, d6, d7, d8, (double) j * d8) / d3;
                }
            }

            if (!flag2) {
                noisegeneratorperlin1 = this.maxLimitNoise.getOctaveNoise(i1);
                if (noisegeneratorperlin1 != null) {
                    d1 += noisegeneratorperlin1.noise(d5, d6, d7, d8, (double) j * d8) / d3;
                }
            }

            d3 /= 2.0D;
        }

        return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, d4) / 128.0D;
    }

    @Override
    public double minValue() {
        return -this.maxValue();
    }

    @Override
    public double maxValue() {
        return this.maxValue;
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringbuilder) {
        stringbuilder.append("BlendedNoise{minLimitNoise=");
        this.minLimitNoise.parityConfigString(stringbuilder);
        stringbuilder.append(", maxLimitNoise=");
        this.maxLimitNoise.parityConfigString(stringbuilder);
        stringbuilder.append(", mainNoise=");
        this.mainNoise.parityConfigString(stringbuilder);
        stringbuilder.append(String.format(", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=%d, cellHeight=%d", this.xzScale, this.yScale, this.xzMainScale, this.yMainScale, this.cellWidth, this.cellHeight)).append('}');
    }

    @Override
    public Codec<? extends DensityFunction> codec() {
        return BlendedNoise.CODEC;
    }
}

package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.stream.IntStream;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class BlendedNoise implements DensityFunction.d {

    private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001D, 1000.0D);
    private static final MapCodec<BlendedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(BlendedNoise.SCALE_RANGE.fieldOf("xz_scale").forGetter((blendednoise) -> {
            return blendednoise.xzScale;
        }), BlendedNoise.SCALE_RANGE.fieldOf("y_scale").forGetter((blendednoise) -> {
            return blendednoise.yScale;
        }), BlendedNoise.SCALE_RANGE.fieldOf("xz_factor").forGetter((blendednoise) -> {
            return blendednoise.xzFactor;
        }), BlendedNoise.SCALE_RANGE.fieldOf("y_factor").forGetter((blendednoise) -> {
            return blendednoise.yFactor;
        }), Codec.doubleRange(1.0D, 8.0D).fieldOf("smear_scale_multiplier").forGetter((blendednoise) -> {
            return blendednoise.smearScaleMultiplier;
        })).apply(instance, BlendedNoise::createUnseeded);
    });
    public static final KeyDispatchDataCodec<BlendedNoise> CODEC = KeyDispatchDataCodec.of(BlendedNoise.DATA_CODEC);
    private final NoiseGeneratorOctaves minLimitNoise;
    private final NoiseGeneratorOctaves maxLimitNoise;
    private final NoiseGeneratorOctaves mainNoise;
    private final double xzMultiplier;
    private final double yMultiplier;
    private final double xzFactor;
    private final double yFactor;
    private final double smearScaleMultiplier;
    private final double maxValue;
    private final double xzScale;
    private final double yScale;

    public static BlendedNoise createUnseeded(double d0, double d1, double d2, double d3, double d4) {
        return new BlendedNoise(new XoroshiroRandomSource(0L), d0, d1, d2, d3, d4);
    }

    private BlendedNoise(NoiseGeneratorOctaves noisegeneratoroctaves, NoiseGeneratorOctaves noisegeneratoroctaves1, NoiseGeneratorOctaves noisegeneratoroctaves2, double d0, double d1, double d2, double d3, double d4) {
        this.minLimitNoise = noisegeneratoroctaves;
        this.maxLimitNoise = noisegeneratoroctaves1;
        this.mainNoise = noisegeneratoroctaves2;
        this.xzScale = d0;
        this.yScale = d1;
        this.xzFactor = d2;
        this.yFactor = d3;
        this.smearScaleMultiplier = d4;
        this.xzMultiplier = 684.412D * this.xzScale;
        this.yMultiplier = 684.412D * this.yScale;
        this.maxValue = noisegeneratoroctaves.maxBrokenValue(this.yMultiplier);
    }

    @VisibleForTesting
    public BlendedNoise(RandomSource randomsource, double d0, double d1, double d2, double d3, double d4) {
        this(NoiseGeneratorOctaves.createLegacyForBlendedNoise(randomsource, IntStream.rangeClosed(-15, 0)), NoiseGeneratorOctaves.createLegacyForBlendedNoise(randomsource, IntStream.rangeClosed(-15, 0)), NoiseGeneratorOctaves.createLegacyForBlendedNoise(randomsource, IntStream.rangeClosed(-7, 0)), d0, d1, d2, d3, d4);
    }

    public BlendedNoise withNewRandom(RandomSource randomsource) {
        return new BlendedNoise(randomsource, this.xzScale, this.yScale, this.xzFactor, this.yFactor, this.smearScaleMultiplier);
    }

    @Override
    public double compute(DensityFunction.b densityfunction_b) {
        double d0 = (double) densityfunction_b.blockX() * this.xzMultiplier;
        double d1 = (double) densityfunction_b.blockY() * this.yMultiplier;
        double d2 = (double) densityfunction_b.blockZ() * this.xzMultiplier;
        double d3 = d0 / this.xzFactor;
        double d4 = d1 / this.yFactor;
        double d5 = d2 / this.xzFactor;
        double d6 = this.yMultiplier * this.smearScaleMultiplier;
        double d7 = d6 / this.yFactor;
        double d8 = 0.0D;
        double d9 = 0.0D;
        double d10 = 0.0D;
        boolean flag = true;
        double d11 = 1.0D;

        for (int i = 0; i < 8; ++i) {
            NoiseGeneratorPerlin noisegeneratorperlin = this.mainNoise.getOctaveNoise(i);

            if (noisegeneratorperlin != null) {
                d10 += noisegeneratorperlin.noise(NoiseGeneratorOctaves.wrap(d3 * d11), NoiseGeneratorOctaves.wrap(d4 * d11), NoiseGeneratorOctaves.wrap(d5 * d11), d7 * d11, d4 * d11) / d11;
            }

            d11 /= 2.0D;
        }

        double d12 = (d10 / 10.0D + 1.0D) / 2.0D;
        boolean flag1 = d12 >= 1.0D;
        boolean flag2 = d12 <= 0.0D;

        d11 = 1.0D;

        for (int j = 0; j < 16; ++j) {
            double d13 = NoiseGeneratorOctaves.wrap(d0 * d11);
            double d14 = NoiseGeneratorOctaves.wrap(d1 * d11);
            double d15 = NoiseGeneratorOctaves.wrap(d2 * d11);
            double d16 = d6 * d11;
            NoiseGeneratorPerlin noisegeneratorperlin1;

            if (!flag1) {
                noisegeneratorperlin1 = this.minLimitNoise.getOctaveNoise(j);
                if (noisegeneratorperlin1 != null) {
                    d8 += noisegeneratorperlin1.noise(d13, d14, d15, d16, d1 * d11) / d11;
                }
            }

            if (!flag2) {
                noisegeneratorperlin1 = this.maxLimitNoise.getOctaveNoise(j);
                if (noisegeneratorperlin1 != null) {
                    d9 += noisegeneratorperlin1.noise(d13, d14, d15, d16, d1 * d11) / d11;
                }
            }

            d11 /= 2.0D;
        }

        return MathHelper.clampedLerp(d8 / 512.0D, d9 / 512.0D, d12) / 128.0D;
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
        stringbuilder.append(String.format(Locale.ROOT, ", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=4, cellHeight=8", 684.412D, 684.412D, 8.555150000000001D, 4.277575000000001D)).append('}');
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return BlendedNoise.CODEC;
    }
}

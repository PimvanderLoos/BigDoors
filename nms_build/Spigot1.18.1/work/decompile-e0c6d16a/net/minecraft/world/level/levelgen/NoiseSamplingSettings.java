package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingSettings {

    private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001D, 1000.0D);
    public static final Codec<NoiseSamplingSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(NoiseSamplingSettings.SCALE_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingSettings::xzScale), NoiseSamplingSettings.SCALE_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingSettings::yScale), NoiseSamplingSettings.SCALE_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingSettings::xzFactor), NoiseSamplingSettings.SCALE_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingSettings::yFactor)).apply(instance, NoiseSamplingSettings::new);
    });
    private final double xzScale;
    private final double yScale;
    private final double xzFactor;
    private final double yFactor;

    public NoiseSamplingSettings(double d0, double d1, double d2, double d3) {
        this.xzScale = d0;
        this.yScale = d1;
        this.xzFactor = d2;
        this.yFactor = d3;
    }

    public double xzScale() {
        return this.xzScale;
    }

    public double yScale() {
        return this.yScale;
    }

    public double xzFactor() {
        return this.xzFactor;
    }

    public double yFactor() {
        return this.yFactor;
    }
}

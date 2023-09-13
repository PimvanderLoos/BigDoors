package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingSettings {

    private static final Codec<Double> SCALE_RANGE = Codec.doubleRange(0.001D, 1000.0D);
    public static final Codec<NoiseSamplingSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(NoiseSamplingSettings.SCALE_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingSettings::a), NoiseSamplingSettings.SCALE_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingSettings::b), NoiseSamplingSettings.SCALE_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingSettings::c), NoiseSamplingSettings.SCALE_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingSettings::d)).apply(instance, NoiseSamplingSettings::new);
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

    public double a() {
        return this.xzScale;
    }

    public double b() {
        return this.yScale;
    }

    public double c() {
        return this.xzFactor;
    }

    public double d() {
        return this.yFactor;
    }
}

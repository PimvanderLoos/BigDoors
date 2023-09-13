package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSamplingSettings {

    private static final Codec<Double> b = Codec.doubleRange(0.001D, 1000.0D);
    public static final Codec<NoiseSamplingSettings> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(NoiseSamplingSettings.b.fieldOf("xz_scale").forGetter(NoiseSamplingSettings::a), NoiseSamplingSettings.b.fieldOf("y_scale").forGetter(NoiseSamplingSettings::b), NoiseSamplingSettings.b.fieldOf("xz_factor").forGetter(NoiseSamplingSettings::c), NoiseSamplingSettings.b.fieldOf("y_factor").forGetter(NoiseSamplingSettings::d)).apply(instance, NoiseSamplingSettings::new);
    });
    private final double c;
    private final double d;
    private final double e;
    private final double f;

    public NoiseSamplingSettings(double d0, double d1, double d2, double d3) {
        this.c = d0;
        this.d = d1;
        this.e = d2;
        this.f = d3;
    }

    public double a() {
        return this.c;
    }

    public double b() {
        return this.d;
    }

    public double c() {
        return this.e;
    }

    public double d() {
        return this.f;
    }
}

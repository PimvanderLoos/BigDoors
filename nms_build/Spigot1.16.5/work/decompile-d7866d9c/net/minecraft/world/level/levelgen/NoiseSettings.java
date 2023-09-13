package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSettings {

    public static final Codec<NoiseSettings> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 256).fieldOf("height").forGetter(NoiseSettings::a), NoiseSamplingSettings.a.fieldOf("sampling").forGetter(NoiseSettings::b), NoiseSlideSettings.a.fieldOf("top_slide").forGetter(NoiseSettings::c), NoiseSlideSettings.a.fieldOf("bottom_slide").forGetter(NoiseSettings::d), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::e), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::f), Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseSettings::g), Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseSettings::h), Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseSettings::i), Codec.BOOL.optionalFieldOf("random_density_offset", false, Lifecycle.experimental()).forGetter(NoiseSettings::j), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(NoiseSettings::k), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(NoiseSettings::l)).apply(instance, NoiseSettings::new);
    });
    private final int b;
    private final NoiseSamplingSettings c;
    private final NoiseSlideSettings d;
    private final NoiseSlideSettings e;
    private final int f;
    private final int g;
    private final double h;
    private final double i;
    private final boolean j;
    private final boolean k;
    private final boolean l;
    private final boolean m;

    public NoiseSettings(int i, NoiseSamplingSettings noisesamplingsettings, NoiseSlideSettings noiseslidesettings, NoiseSlideSettings noiseslidesettings1, int j, int k, double d0, double d1, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.b = i;
        this.c = noisesamplingsettings;
        this.d = noiseslidesettings;
        this.e = noiseslidesettings1;
        this.f = j;
        this.g = k;
        this.h = d0;
        this.i = d1;
        this.j = flag;
        this.k = flag1;
        this.l = flag2;
        this.m = flag3;
    }

    public int a() {
        return this.b;
    }

    public NoiseSamplingSettings b() {
        return this.c;
    }

    public NoiseSlideSettings c() {
        return this.d;
    }

    public NoiseSlideSettings d() {
        return this.e;
    }

    public int e() {
        return this.f;
    }

    public int f() {
        return this.g;
    }

    public double g() {
        return this.h;
    }

    public double h() {
        return this.i;
    }

    @Deprecated
    public boolean i() {
        return this.j;
    }

    @Deprecated
    public boolean j() {
        return this.k;
    }

    @Deprecated
    public boolean k() {
        return this.l;
    }

    @Deprecated
    public boolean l() {
        return this.m;
    }
}

package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.dimension.DimensionManager;

public class NoiseSettings {

    public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::a), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::b), NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::c), NoiseSlideSettings.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::d), NoiseSlideSettings.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::e), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::f), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::g), Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseSettings::h), Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseSettings::i), Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseSettings::j), Codec.BOOL.optionalFieldOf("random_density_offset", false, Lifecycle.experimental()).forGetter(NoiseSettings::k), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(NoiseSettings::l), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(NoiseSettings::m)).apply(instance, NoiseSettings::new);
    }).comapFlatMap(NoiseSettings::a, Function.identity());
    private final int minY;
    private final int height;
    private final NoiseSamplingSettings noiseSamplingSettings;
    private final NoiseSlideSettings topSlideSettings;
    private final NoiseSlideSettings bottomSlideSettings;
    private final int noiseSizeHorizontal;
    private final int noiseSizeVertical;
    private final double densityFactor;
    private final double densityOffset;
    private final boolean useSimplexSurfaceNoise;
    private final boolean randomDensityOffset;
    private final boolean islandNoiseOverride;
    private final boolean isAmplified;

    private static DataResult<NoiseSettings> a(NoiseSettings noisesettings) {
        return noisesettings.a() + noisesettings.b() > DimensionManager.MAX_Y + 1 ? DataResult.error("min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1)) : (noisesettings.b() % 16 != 0 ? DataResult.error("height has to be a multiple of 16") : (noisesettings.a() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(noisesettings)));
    }

    private NoiseSettings(int i, int j, NoiseSamplingSettings noisesamplingsettings, NoiseSlideSettings noiseslidesettings, NoiseSlideSettings noiseslidesettings1, int k, int l, double d0, double d1, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.minY = i;
        this.height = j;
        this.noiseSamplingSettings = noisesamplingsettings;
        this.topSlideSettings = noiseslidesettings;
        this.bottomSlideSettings = noiseslidesettings1;
        this.noiseSizeHorizontal = k;
        this.noiseSizeVertical = l;
        this.densityFactor = d0;
        this.densityOffset = d1;
        this.useSimplexSurfaceNoise = flag;
        this.randomDensityOffset = flag1;
        this.islandNoiseOverride = flag2;
        this.isAmplified = flag3;
    }

    public static NoiseSettings a(int i, int j, NoiseSamplingSettings noisesamplingsettings, NoiseSlideSettings noiseslidesettings, NoiseSlideSettings noiseslidesettings1, int k, int l, double d0, double d1, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        NoiseSettings noisesettings = new NoiseSettings(i, j, noisesamplingsettings, noiseslidesettings, noiseslidesettings1, k, l, d0, d1, flag, flag1, flag2, flag3);

        a(noisesettings).error().ifPresent((partialresult) -> {
            throw new IllegalStateException(partialresult.message());
        });
        return noisesettings;
    }

    public int a() {
        return this.minY;
    }

    public int b() {
        return this.height;
    }

    public NoiseSamplingSettings c() {
        return this.noiseSamplingSettings;
    }

    public NoiseSlideSettings d() {
        return this.topSlideSettings;
    }

    public NoiseSlideSettings e() {
        return this.bottomSlideSettings;
    }

    public int f() {
        return this.noiseSizeHorizontal;
    }

    public int g() {
        return this.noiseSizeVertical;
    }

    public double h() {
        return this.densityFactor;
    }

    public double i() {
        return this.densityOffset;
    }

    @Deprecated
    public boolean j() {
        return this.useSimplexSurfaceNoise;
    }

    @Deprecated
    public boolean k() {
        return this.randomDensityOffset;
    }

    @Deprecated
    public boolean l() {
        return this.islandNoiseOverride;
    }

    @Deprecated
    public boolean m() {
        return this.isAmplified;
    }
}

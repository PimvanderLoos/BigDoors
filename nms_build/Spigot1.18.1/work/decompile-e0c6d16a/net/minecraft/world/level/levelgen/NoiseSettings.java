package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionManager;

public record NoiseSettings(int b, int c, NoiseSamplingSettings d, NoiseSlider e, NoiseSlider f, int g, int h, boolean i, boolean j, boolean k, TerrainShaper l) {

    private final int minY;
    private final int height;
    private final NoiseSamplingSettings noiseSamplingSettings;
    private final NoiseSlider topSlideSettings;
    private final NoiseSlider bottomSlideSettings;
    private final int noiseSizeHorizontal;
    private final int noiseSizeVertical;
    private final boolean islandNoiseOverride;
    private final boolean isAmplified;
    private final boolean largeBiomes;
    private final TerrainShaper terrainShaper;
    public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), NoiseSlider.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), NoiseSlider.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), Codec.BOOL.optionalFieldOf("island_noise_override", false, Lifecycle.experimental()).forGetter(NoiseSettings::islandNoiseOverride), Codec.BOOL.optionalFieldOf("amplified", false, Lifecycle.experimental()).forGetter(NoiseSettings::isAmplified), Codec.BOOL.optionalFieldOf("large_biomes", false, Lifecycle.experimental()).forGetter(NoiseSettings::largeBiomes), TerrainShaper.CODEC.fieldOf("terrain_shaper").forGetter(NoiseSettings::terrainShaper)).apply(instance, NoiseSettings::new);
    }).comapFlatMap(NoiseSettings::guardY, Function.identity());

    public NoiseSettings(int i, int j, NoiseSamplingSettings noisesamplingsettings, NoiseSlider noiseslider, NoiseSlider noiseslider1, int k, int l, boolean flag, boolean flag1, boolean flag2, TerrainShaper terrainshaper) {
        this.minY = i;
        this.height = j;
        this.noiseSamplingSettings = noisesamplingsettings;
        this.topSlideSettings = noiseslider;
        this.bottomSlideSettings = noiseslider1;
        this.noiseSizeHorizontal = k;
        this.noiseSizeVertical = l;
        this.islandNoiseOverride = flag;
        this.isAmplified = flag1;
        this.largeBiomes = flag2;
        this.terrainShaper = terrainshaper;
    }

    private static DataResult<NoiseSettings> guardY(NoiseSettings noisesettings) {
        return noisesettings.minY() + noisesettings.height() > DimensionManager.MAX_Y + 1 ? DataResult.error("min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1)) : (noisesettings.height() % 16 != 0 ? DataResult.error("height has to be a multiple of 16") : (noisesettings.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(noisesettings)));
    }

    public static NoiseSettings create(int i, int j, NoiseSamplingSettings noisesamplingsettings, NoiseSlider noiseslider, NoiseSlider noiseslider1, int k, int l, boolean flag, boolean flag1, boolean flag2, TerrainShaper terrainshaper) {
        NoiseSettings noisesettings = new NoiseSettings(i, j, noisesamplingsettings, noiseslider, noiseslider1, k, l, flag, flag1, flag2, terrainshaper);

        guardY(noisesettings).error().ifPresent((partialresult) -> {
            throw new IllegalStateException(partialresult.message());
        });
        return noisesettings;
    }

    /** @deprecated */
    @Deprecated
    public boolean islandNoiseOverride() {
        return this.islandNoiseOverride;
    }

    /** @deprecated */
    @Deprecated
    public boolean isAmplified() {
        return this.isAmplified;
    }

    /** @deprecated */
    @Deprecated
    public boolean largeBiomes() {
        return this.largeBiomes;
    }

    public int getCellHeight() {
        return QuartPos.toBlock(this.noiseSizeVertical());
    }

    public int getCellWidth() {
        return QuartPos.toBlock(this.noiseSizeHorizontal());
    }

    public int getCellCountY() {
        return this.height() / this.getCellHeight();
    }

    public int getMinCellY() {
        return MathHelper.intFloorDiv(this.minY(), this.getCellHeight());
    }

    public int minY() {
        return this.minY;
    }

    public int height() {
        return this.height;
    }

    public NoiseSamplingSettings noiseSamplingSettings() {
        return this.noiseSamplingSettings;
    }

    public NoiseSlider topSlideSettings() {
        return this.topSlideSettings;
    }

    public NoiseSlider bottomSlideSettings() {
        return this.bottomSlideSettings;
    }

    public int noiseSizeHorizontal() {
        return this.noiseSizeHorizontal;
    }

    public int noiseSizeVertical() {
        return this.noiseSizeVertical;
    }

    public TerrainShaper terrainShaper() {
        return this.terrainShaper;
    }
}

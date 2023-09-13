package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionManager;

public record NoiseSettings(int f, int g, NoiseSamplingSettings h, NoiseSlider i, NoiseSlider j, int k, int l, TerrainShaper m) {

    private final int minY;
    private final int height;
    private final NoiseSamplingSettings noiseSamplingSettings;
    private final NoiseSlider topSlideSettings;
    private final NoiseSlider bottomSlideSettings;
    private final int noiseSizeHorizontal;
    private final int noiseSizeVertical;
    private final TerrainShaper terrainShaper;
    public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), NoiseSlider.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), NoiseSlider.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), TerrainShaper.CODEC.fieldOf("terrain_shaper").forGetter(NoiseSettings::terrainShaper)).apply(instance, NoiseSettings::new);
    }).comapFlatMap(NoiseSettings::guardY, Function.identity());
    static final NoiseSettings NETHER_NOISE_SETTINGS = create(0, 128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, TerrainProvider.nether());
    static final NoiseSettings END_NOISE_SETTINGS = create(0, 128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, TerrainProvider.end());
    static final NoiseSettings CAVES_NOISE_SETTINGS = create(-64, 192, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, TerrainProvider.caves());
    static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = create(0, 256, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, TerrainProvider.floatingIslands());

    public NoiseSettings(int i, int j, NoiseSamplingSettings noisesamplingsettings, NoiseSlider noiseslider, NoiseSlider noiseslider1, int k, int l, TerrainShaper terrainshaper) {
        this.minY = i;
        this.height = j;
        this.noiseSamplingSettings = noisesamplingsettings;
        this.topSlideSettings = noiseslider;
        this.bottomSlideSettings = noiseslider1;
        this.noiseSizeHorizontal = k;
        this.noiseSizeVertical = l;
        this.terrainShaper = terrainshaper;
    }

    private static DataResult<NoiseSettings> guardY(NoiseSettings noisesettings) {
        return noisesettings.minY() + noisesettings.height() > DimensionManager.MAX_Y + 1 ? DataResult.error("min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1)) : (noisesettings.height() % 16 != 0 ? DataResult.error("height has to be a multiple of 16") : (noisesettings.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(noisesettings)));
    }

    public static NoiseSettings create(int i, int j, NoiseSamplingSettings noisesamplingsettings, NoiseSlider noiseslider, NoiseSlider noiseslider1, int k, int l, TerrainShaper terrainshaper) {
        NoiseSettings noisesettings = new NoiseSettings(i, j, noisesamplingsettings, noiseslider, noiseslider1, k, l, terrainshaper);

        guardY(noisesettings).error().ifPresent((partialresult) -> {
            throw new IllegalStateException(partialresult.message());
        });
        return noisesettings;
    }

    static NoiseSettings overworldNoiseSettings(boolean flag) {
        return create(-64, 384, new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-0.078125D, 2, flag ? 0 : 8), new NoiseSlider(flag ? 0.4D : 0.1171875D, 3, 0), 1, 2, TerrainProvider.overworld(flag));
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

package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.dimension.DimensionManager;

public record NoiseSettings(int minY, int height, int noiseSizeHorizontal, int noiseSizeVertical) {

    public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical)).apply(instance, NoiseSettings::new);
    }).comapFlatMap(NoiseSettings::guardY, Function.identity());
    protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = create(-64, 384, 1, 2);
    protected static final NoiseSettings NETHER_NOISE_SETTINGS = create(0, 128, 1, 2);
    protected static final NoiseSettings END_NOISE_SETTINGS = create(0, 128, 2, 1);
    protected static final NoiseSettings CAVES_NOISE_SETTINGS = create(-64, 192, 1, 2);
    protected static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = create(0, 256, 2, 1);

    private static DataResult<NoiseSettings> guardY(NoiseSettings noisesettings) {
        return noisesettings.minY() + noisesettings.height() > DimensionManager.MAX_Y + 1 ? DataResult.error(() -> {
            return "min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1);
        }) : (noisesettings.height() % 16 != 0 ? DataResult.error(() -> {
            return "height has to be a multiple of 16";
        }) : (noisesettings.minY() % 16 != 0 ? DataResult.error(() -> {
            return "min_y has to be a multiple of 16";
        }) : DataResult.success(noisesettings)));
    }

    public static NoiseSettings create(int i, int j, int k, int l) {
        NoiseSettings noisesettings = new NoiseSettings(i, j, k, l);

        guardY(noisesettings).error().ifPresent((partialresult) -> {
            throw new IllegalStateException(partialresult.message());
        });
        return noisesettings;
    }

    public int getCellHeight() {
        return QuartPos.toBlock(this.noiseSizeVertical());
    }

    public int getCellWidth() {
        return QuartPos.toBlock(this.noiseSizeHorizontal());
    }

    public NoiseSettings clampToHeightAccessor(LevelHeightAccessor levelheightaccessor) {
        int i = Math.max(this.minY, levelheightaccessor.getMinBuildHeight());
        int j = Math.min(this.minY + this.height, levelheightaccessor.getMaxBuildHeight()) - i;

        return new NoiseSettings(i, j, this.noiseSizeHorizontal, this.noiseSizeVertical);
    }
}

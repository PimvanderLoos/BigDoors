package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeBase;

public class NoiseThresholdCountPlacement extends RepeatingPlacement {

    public static final Codec<NoiseThresholdCountPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((noisethresholdcountplacement) -> {
            return noisethresholdcountplacement.noiseLevel;
        }), Codec.INT.fieldOf("below_noise").forGetter((noisethresholdcountplacement) -> {
            return noisethresholdcountplacement.belowNoise;
        }), Codec.INT.fieldOf("above_noise").forGetter((noisethresholdcountplacement) -> {
            return noisethresholdcountplacement.aboveNoise;
        })).apply(instance, NoiseThresholdCountPlacement::new);
    });
    private final double noiseLevel;
    private final int belowNoise;
    private final int aboveNoise;

    private NoiseThresholdCountPlacement(double d0, int i, int j) {
        this.noiseLevel = d0;
        this.belowNoise = i;
        this.aboveNoise = j;
    }

    public static NoiseThresholdCountPlacement of(double d0, int i, int j) {
        return new NoiseThresholdCountPlacement(d0, i, j);
    }

    @Override
    protected int count(RandomSource randomsource, BlockPosition blockposition) {
        double d0 = BiomeBase.BIOME_INFO_NOISE.getValue((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);

        return d0 < this.noiseLevel ? this.belowNoise : this.aboveNoise;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.NOISE_THRESHOLD_COUNT;
    }
}

package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeBase;

public class NoiseBasedCountPlacement extends RepeatingPlacement {

    public static final Codec<NoiseBasedCountPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((noisebasedcountplacement) -> {
            return noisebasedcountplacement.noiseToCountRatio;
        }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((noisebasedcountplacement) -> {
            return noisebasedcountplacement.noiseFactor;
        }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((noisebasedcountplacement) -> {
            return noisebasedcountplacement.noiseOffset;
        })).apply(instance, NoiseBasedCountPlacement::new);
    });
    private final int noiseToCountRatio;
    private final double noiseFactor;
    private final double noiseOffset;

    private NoiseBasedCountPlacement(int i, double d0, double d1) {
        this.noiseToCountRatio = i;
        this.noiseFactor = d0;
        this.noiseOffset = d1;
    }

    public static NoiseBasedCountPlacement of(int i, double d0, double d1) {
        return new NoiseBasedCountPlacement(i, d0, d1);
    }

    @Override
    protected int count(RandomSource randomsource, BlockPosition blockposition) {
        double d0 = BiomeBase.BIOME_INFO_NOISE.getValue((double) blockposition.getX() / this.noiseFactor, (double) blockposition.getZ() / this.noiseFactor, false);

        return (int) Math.ceil((d0 + this.noiseOffset) * (double) this.noiseToCountRatio);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.NOISE_BASED_COUNT;
    }
}

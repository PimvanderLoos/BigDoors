package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;

public class NoiseSlider {

    public static final Codec<NoiseSlider> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("target").forGetter((noiseslider) -> {
            return noiseslider.target;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter((noiseslider) -> {
            return noiseslider.size;
        }), Codec.INT.fieldOf("offset").forGetter((noiseslider) -> {
            return noiseslider.offset;
        })).apply(instance, NoiseSlider::new);
    });
    private final double target;
    private final int size;
    private final int offset;

    public NoiseSlider(double d0, int i, int j) {
        this.target = d0;
        this.size = i;
        this.offset = j;
    }

    public double applySlide(double d0, int i) {
        if (this.size <= 0) {
            return d0;
        } else {
            double d1 = (double) (i - this.offset) / (double) this.size;

            return MathHelper.clampedLerp(this.target, d0, d1);
        }
    }
}

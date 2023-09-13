package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;

public record NoiseSlider(double b, int c, int d) {

    private final double target;
    private final int size;
    private final int offset;
    public static final Codec<NoiseSlider> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("target").forGetter((noiseslider) -> {
            return noiseslider.target;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter((noiseslider) -> {
            return noiseslider.size;
        }), Codec.INT.fieldOf("offset").forGetter((noiseslider) -> {
            return noiseslider.offset;
        })).apply(instance, NoiseSlider::new);
    });

    public NoiseSlider(double d0, int i, int j) {
        this.target = d0;
        this.size = i;
        this.offset = j;
    }

    public double applySlide(double d0, double d1) {
        if (this.size <= 0) {
            return d0;
        } else {
            double d2 = (d1 - (double) this.offset) / (double) this.size;

            return MathHelper.clampedLerp(this.target, d0, d2);
        }
    }

    public double target() {
        return this.target;
    }

    public int size() {
        return this.size;
    }

    public int offset() {
        return this.offset;
    }
}

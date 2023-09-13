package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public class ClampedInt extends IntProvider {

    public static final Codec<ClampedInt> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IntProvider.CODEC.fieldOf("source").forGetter((clampedint) -> {
            return clampedint.source;
        }), Codec.INT.fieldOf("min_inclusive").forGetter((clampedint) -> {
            return clampedint.minInclusive;
        }), Codec.INT.fieldOf("max_inclusive").forGetter((clampedint) -> {
            return clampedint.maxInclusive;
        })).apply(instance, ClampedInt::new);
    }).comapFlatMap((clampedint) -> {
        return clampedint.maxInclusive < clampedint.minInclusive ? DataResult.error(() -> {
            return "Max must be at least min, min_inclusive: " + clampedint.minInclusive + ", max_inclusive: " + clampedint.maxInclusive;
        }) : DataResult.success(clampedint);
    }, Function.identity());
    private final IntProvider source;
    private final int minInclusive;
    private final int maxInclusive;

    public static ClampedInt of(IntProvider intprovider, int i, int j) {
        return new ClampedInt(intprovider, i, j);
    }

    public ClampedInt(IntProvider intprovider, int i, int j) {
        this.source = intprovider;
        this.minInclusive = i;
        this.maxInclusive = j;
    }

    @Override
    public int sample(RandomSource randomsource) {
        return MathHelper.clamp(this.source.sample(randomsource), this.minInclusive, this.maxInclusive);
    }

    @Override
    public int getMinValue() {
        return Math.max(this.minInclusive, this.source.getMinValue());
    }

    @Override
    public int getMaxValue() {
        return Math.min(this.maxInclusive, this.source.getMaxValue());
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED;
    }
}

package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.RandomSource;

public class BiasedToBottomInt extends IntProvider {

    public static final Codec<BiasedToBottomInt> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("min_inclusive").forGetter((biasedtobottomint) -> {
            return biasedtobottomint.minInclusive;
        }), Codec.INT.fieldOf("max_inclusive").forGetter((biasedtobottomint) -> {
            return biasedtobottomint.maxInclusive;
        })).apply(instance, BiasedToBottomInt::new);
    }).comapFlatMap((biasedtobottomint) -> {
        return biasedtobottomint.maxInclusive < biasedtobottomint.minInclusive ? DataResult.error(() -> {
            return "Max must be at least min, min_inclusive: " + biasedtobottomint.minInclusive + ", max_inclusive: " + biasedtobottomint.maxInclusive;
        }) : DataResult.success(biasedtobottomint);
    }, Function.identity());
    private final int minInclusive;
    private final int maxInclusive;

    private BiasedToBottomInt(int i, int j) {
        this.minInclusive = i;
        this.maxInclusive = j;
    }

    public static BiasedToBottomInt of(int i, int j) {
        return new BiasedToBottomInt(i, j);
    }

    @Override
    public int sample(RandomSource randomsource) {
        return this.minInclusive + randomsource.nextInt(randomsource.nextInt(this.maxInclusive - this.minInclusive + 1) + 1);
    }

    @Override
    public int getMinValue() {
        return this.minInclusive;
    }

    @Override
    public int getMaxValue() {
        return this.maxInclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}

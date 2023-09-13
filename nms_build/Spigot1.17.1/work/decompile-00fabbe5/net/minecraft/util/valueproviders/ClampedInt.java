package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.MathHelper;

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
        return clampedint.maxInclusive < clampedint.minInclusive ? DataResult.error("Max must be at least min, min_inclusive: " + clampedint.minInclusive + ", max_inclusive: " + clampedint.maxInclusive) : DataResult.success(clampedint);
    }, Function.identity());
    private final IntProvider source;
    private int minInclusive;
    private int maxInclusive;

    public static ClampedInt a(IntProvider intprovider, int i, int j) {
        return new ClampedInt(intprovider, i, j);
    }

    public ClampedInt(IntProvider intprovider, int i, int j) {
        this.source = intprovider;
        this.minInclusive = i;
        this.maxInclusive = j;
    }

    @Override
    public int a(Random random) {
        return MathHelper.clamp(this.source.a(random), this.minInclusive, this.maxInclusive);
    }

    @Override
    public int a() {
        return Math.max(this.minInclusive, this.source.a());
    }

    @Override
    public int b() {
        return Math.min(this.maxInclusive, this.source.b());
    }

    @Override
    public IntProviderType<?> c() {
        return IntProviderType.CLAMPED;
    }
}

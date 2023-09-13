package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.MathHelper;

public class UniformInt extends IntProvider {

    public static final Codec<UniformInt> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("min_inclusive").forGetter((uniformint) -> {
            return uniformint.minInclusive;
        }), Codec.INT.fieldOf("max_inclusive").forGetter((uniformint) -> {
            return uniformint.maxInclusive;
        })).apply(instance, UniformInt::new);
    }).comapFlatMap((uniformint) -> {
        return uniformint.maxInclusive < uniformint.minInclusive ? DataResult.error("Max must be at least min, min_inclusive: " + uniformint.minInclusive + ", max_inclusive: " + uniformint.maxInclusive) : DataResult.success(uniformint);
    }, Function.identity());
    private final int minInclusive;
    private final int maxInclusive;

    private UniformInt(int i, int j) {
        this.minInclusive = i;
        this.maxInclusive = j;
    }

    public static UniformInt a(int i, int j) {
        return new UniformInt(i, j);
    }

    @Override
    public int a(Random random) {
        return MathHelper.b(random, this.minInclusive, this.maxInclusive);
    }

    @Override
    public int a() {
        return this.minInclusive;
    }

    @Override
    public int b() {
        return this.maxInclusive;
    }

    @Override
    public IntProviderType<?> c() {
        return IntProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}

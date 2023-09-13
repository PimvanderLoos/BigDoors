package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public record InclusiveRange<T extends Comparable<T>> (T minInclusive, T maxInclusive) {

    public static final Codec<InclusiveRange<Integer>> INT = codec(Codec.INT);

    public InclusiveRange(T t0, T t1) {
        if (t0.compareTo(t1) > 0) {
            throw new IllegalArgumentException("min_inclusive must be less than or equal to max_inclusive");
        } else {
            this.minInclusive = t0;
            this.maxInclusive = t1;
        }
    }

    public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> codec) {
        return ExtraCodecs.intervalCodec(codec, "min_inclusive", "max_inclusive", InclusiveRange::create, InclusiveRange::minInclusive, InclusiveRange::maxInclusive);
    }

    public static <T extends Comparable<T>> Codec<InclusiveRange<T>> codec(Codec<T> codec, T t0, T t1) {
        return ExtraCodecs.validate(codec(codec), (inclusiverange) -> {
            return inclusiverange.minInclusive().compareTo(t0) < 0 ? DataResult.error(() -> {
                return "Range limit too low, expected at least " + t0 + " [" + inclusiverange.minInclusive() + "-" + inclusiverange.maxInclusive() + "]";
            }) : (inclusiverange.maxInclusive().compareTo(t1) > 0 ? DataResult.error(() -> {
                return "Range limit too high, expected at most " + t1 + " [" + inclusiverange.minInclusive() + "-" + inclusiverange.maxInclusive() + "]";
            }) : DataResult.success(inclusiverange));
        });
    }

    public static <T extends Comparable<T>> DataResult<InclusiveRange<T>> create(T t0, T t1) {
        return t0.compareTo(t1) <= 0 ? DataResult.success(new InclusiveRange<>(t0, t1)) : DataResult.error(() -> {
            return "min_inclusive must be less than or equal to max_inclusive";
        });
    }

    public boolean isValueInRange(T t0) {
        return t0.compareTo(this.minInclusive) >= 0 && t0.compareTo(this.maxInclusive) <= 0;
    }

    public boolean contains(InclusiveRange<T> inclusiverange) {
        return inclusiverange.minInclusive().compareTo(this.minInclusive) >= 0 && inclusiverange.maxInclusive.compareTo(this.maxInclusive) <= 0;
    }

    public String toString() {
        return "[" + this.minInclusive + ", " + this.maxInclusive + "]";
    }
}

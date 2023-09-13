package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExtraCodecs {

    public static final Codec<Integer> NON_NEGATIVE_INT = a(0, Integer.MAX_VALUE, (integer) -> {
        return "Value must be non-negative: " + integer;
    });
    public static final Codec<Integer> POSITIVE_INT = a(1, Integer.MAX_VALUE, (integer) -> {
        return "Value must be positive: " + integer;
    });

    public ExtraCodecs() {}

    public static <F, S> Codec<Either<F, S>> a(Codec<F> codec, Codec<S> codec1) {
        return new ExtraCodecs.a<>(codec, codec1);
    }

    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> a(N n0, N n1, Function<N, String> function) {
        return (number) -> {
            return ((Comparable) number).compareTo(n0) >= 0 && ((Comparable) number).compareTo(n1) <= 0 ? DataResult.success(number) : DataResult.error((String) function.apply(number));
        };
    }

    private static Codec<Integer> a(int i, int j, Function<Integer, String> function) {
        Function<Integer, DataResult<Integer>> function1 = a(i, j, function);

        return Codec.INT.flatXmap(function1, function1);
    }

    public static <T> Function<List<T>, DataResult<List<T>>> a() {
        return (list) -> {
            return list.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(list);
        };
    }

    public static <T> Codec<List<T>> a(Codec<List<T>> codec) {
        return codec.flatXmap(a(), a());
    }

    public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> b() {
        return (list) -> {
            List<String> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                Supplier supplier = (Supplier) list.get(i);

                try {
                    if (supplier.get() == null) {
                        list1.add("Missing value [" + i + "] : " + supplier);
                    }
                } catch (Exception exception) {
                    list1.add("Invalid value [" + i + "]: " + supplier + ", message: " + exception.getMessage());
                }
            }

            return !list1.isEmpty() ? DataResult.error(String.join("; ", list1)) : DataResult.success(list, Lifecycle.stable());
        };
    }

    public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> c() {
        return (supplier) -> {
            try {
                if (supplier.get() == null) {
                    return DataResult.error("Missing value: " + supplier);
                }
            } catch (Exception exception) {
                return DataResult.error("Invalid value: " + supplier + ", message: " + exception.getMessage());
            }

            return DataResult.success(supplier, Lifecycle.stable());
        };
    }

    private static final class a<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public a(Codec<F> codec, Codec<S> codec1) {
            this.first = codec;
            this.second = codec1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicops, T t0) {
            DataResult<Pair<Either<F, S>, T>> dataresult = this.first.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::left);
            });
            DataResult<Pair<Either<F, S>, T>> dataresult1 = this.second.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::right);
            });
            Optional<Pair<Either<F, S>, T>> optional = dataresult.result();
            Optional<Pair<Either<F, S>, T>> optional1 = dataresult1.result();

            return optional.isPresent() && optional1.isPresent() ? DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional1.get(), (Pair) optional.get()) : (optional.isPresent() ? dataresult : dataresult1);
        }

        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicops, T t0) {
            return (DataResult) either.map((object) -> {
                return this.first.encode(object, dynamicops, t0);
            }, (object) -> {
                return this.second.encode(object, dynamicops, t0);
            });
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ExtraCodecs.a<?, ?> extracodecs_a = (ExtraCodecs.a) object;

                return Objects.equals(this.first, extracodecs_a.first) && Objects.equals(this.second, extracodecs_a.second);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.first, this.second});
        }

        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }
    }
}

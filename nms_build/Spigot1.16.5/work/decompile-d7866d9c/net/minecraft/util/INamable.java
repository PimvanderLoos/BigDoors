package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface INamable {

    String getName();

    static <E extends Enum<E> & INamable> Codec<E> a(Supplier<E[]> supplier, Function<? super String, ? extends E> function) {
        E[] ae = (Enum[]) supplier.get();

        return a(Enum::ordinal, (i) -> {
            return ae[i];
        }, function);
    }

    static <E extends INamable> Codec<E> a(final ToIntFunction<E> tointfunction, final IntFunction<E> intfunction, final Function<? super String, ? extends E> function) {
        return new Codec<E>() {
            public <T> DataResult<T> encode(E e0, DynamicOps<T> dynamicops, T t0) {
                return dynamicops.compressMaps() ? dynamicops.mergeToPrimitive(t0, dynamicops.createInt(tointfunction.applyAsInt(e0))) : dynamicops.mergeToPrimitive(t0, dynamicops.createString(e0.getName()));
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicops, T t0) {
                return dynamicops.compressMaps() ? dynamicops.getNumberValue(t0).flatMap((number) -> {
                    return (DataResult) Optional.ofNullable(intfunction.apply(number.intValue())).map(DataResult::success).orElseGet(() -> {
                        return DataResult.error("Unknown element id: " + number);
                    });
                }).map((inamable) -> {
                    return Pair.of(inamable, dynamicops.empty());
                }) : dynamicops.getStringValue(t0).flatMap((s) -> {
                    return (DataResult) Optional.ofNullable(function.apply(s)).map(DataResult::success).orElseGet(() -> {
                        return DataResult.error("Unknown element name: " + s);
                    });
                }).map((inamable) -> {
                    return Pair.of(inamable, dynamicops.empty());
                });
            }

            public String toString() {
                return "StringRepresentable[" + tointfunction + "]";
            }
        };
    }

    static Keyable a(final INamable[] ainamable) {
        return new Keyable() {
            public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
                if (dynamicops.compressMaps()) {
                    IntStream intstream = IntStream.range(0, ainamable.length);

                    dynamicops.getClass();
                    return intstream.mapToObj(dynamicops::createInt);
                } else {
                    Stream stream = Arrays.stream(ainamable).map(INamable::getName);

                    dynamicops.getClass();
                    return stream.map(dynamicops::createString);
                }
            }
        };
    }
}

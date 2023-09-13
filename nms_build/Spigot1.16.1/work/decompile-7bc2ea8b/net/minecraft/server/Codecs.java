package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Codecs {

    private static Function<Integer, DataResult<Integer>> b(int i, int j) {
        return (integer) -> {
            return integer >= i && integer <= j ? DataResult.success(integer) : DataResult.error("Value " + integer + " outside of range [" + i + ":" + j + "]", integer);
        };
    }

    public static Codec<Integer> a(int i, int j) {
        Function<Integer, DataResult<Integer>> function = b(i, j);

        return Codec.INT.flatXmap(function, function);
    }

    private static Function<Double, DataResult<Double>> b(double d0, double d1) {
        return (odouble) -> {
            return odouble >= d0 && odouble <= d1 ? DataResult.success(odouble) : DataResult.error("Value " + odouble + " outside of range [" + d0 + ":" + d1 + "]", odouble);
        };
    }

    public static Codec<Double> a(double d0, double d1) {
        Function<Double, DataResult<Double>> function = b(d0, d1);

        return Codec.DOUBLE.flatXmap(function, function);
    }

    public static <T> MapCodec<Pair<ResourceKey<T>, T>> a(ResourceKey<IRegistry<T>> resourcekey, MapCodec<T> mapcodec) {
        return Codec.mapPair(MinecraftKey.a.xmap(ResourceKey.a(resourcekey), ResourceKey::a).fieldOf("name"), mapcodec);
    }

    private static <A> MapCodec<A> a(final MapCodec<A> mapcodec, final Codecs.a<A> codecs_a) {
        return new MapCodec<A>() {
            public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
                return mapcodec.keys(dynamicops);
            }

            public <T> RecordBuilder<T> encode(A a0, DynamicOps<T> dynamicops, RecordBuilder<T> recordbuilder) {
                return codecs_a.a(dynamicops, a0, mapcodec.encode(a0, dynamicops, recordbuilder));
            }

            public <T> DataResult<A> decode(DynamicOps<T> dynamicops, MapLike<T> maplike) {
                return codecs_a.a(dynamicops, maplike, mapcodec.decode(dynamicops, maplike));
            }

            public String toString() {
                return mapcodec + "[mapResult " + codecs_a + "]";
            }
        };
    }

    public static <A> MapCodec<A> a(MapCodec<A> mapcodec, final Consumer<String> consumer, final Supplier<? extends A> supplier) {
        return a(mapcodec, new Codecs.a<A>() {
            @Override
            public <T> DataResult<A> a(DynamicOps<T> dynamicops, MapLike<T> maplike, DataResult<A> dataresult) {
                return DataResult.success(dataresult.resultOrPartial(consumer).orElseGet(supplier));
            }

            @Override
            public <T> RecordBuilder<T> a(DynamicOps<T> dynamicops, A a0, RecordBuilder<T> recordbuilder) {
                return recordbuilder;
            }

            public String toString() {
                return "WithDefault[" + supplier.get() + "]";
            }
        });
    }

    public static <A> MapCodec<A> a(MapCodec<A> mapcodec, final Supplier<A> supplier) {
        return a(mapcodec, new Codecs.a<A>() {
            @Override
            public <T> DataResult<A> a(DynamicOps<T> dynamicops, MapLike<T> maplike, DataResult<A> dataresult) {
                return dataresult.setPartial(supplier);
            }

            @Override
            public <T> RecordBuilder<T> a(DynamicOps<T> dynamicops, A a0, RecordBuilder<T> recordbuilder) {
                return recordbuilder;
            }

            public String toString() {
                return "SetPartial[" + supplier + "]";
            }
        });
    }

    interface a<A> {

        <T> DataResult<A> a(DynamicOps<T> dynamicops, MapLike<T> maplike, DataResult<A> dataresult);

        <T> RecordBuilder<T> a(DynamicOps<T> dynamicops, A a0, RecordBuilder<T> recordbuilder);
    }
}

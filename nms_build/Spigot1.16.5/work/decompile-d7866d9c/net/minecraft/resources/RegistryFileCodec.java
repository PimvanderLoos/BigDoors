package net.minecraft.resources;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;

public final class RegistryFileCodec<E> implements Codec<Supplier<E>> {

    private final ResourceKey<? extends IRegistry<E>> a;
    private final Codec<E> b;
    private final boolean c;

    public static <E> RegistryFileCodec<E> a(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        return a(resourcekey, codec, true);
    }

    public static <E> Codec<List<Supplier<E>>> b(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        return Codec.either(a(resourcekey, codec, false).listOf(), codec.xmap((object) -> {
            return () -> {
                return object;
            };
        }, Supplier::get).listOf()).xmap((either) -> {
            return (List) either.map((list) -> {
                return list;
            }, (list) -> {
                return list;
            });
        }, Either::left);
    }

    private static <E> RegistryFileCodec<E> a(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        return new RegistryFileCodec<>(resourcekey, codec, flag);
    }

    private RegistryFileCodec(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        this.a = resourcekey;
        this.b = codec;
        this.c = flag;
    }

    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicops, T t0) {
        return dynamicops instanceof RegistryWriteOps ? ((RegistryWriteOps) dynamicops).a(supplier.get(), t0, this.a, this.b) : this.b.encode(supplier.get(), dynamicops, t0);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        return dynamicops instanceof RegistryReadOps ? ((RegistryReadOps) dynamicops).a(t0, this.a, this.b, this.c) : this.b.decode(dynamicops, t0).map((pair) -> {
            return pair.mapFirst((object) -> {
                return () -> {
                    return object;
                };
            });
        });
    }

    public String toString() {
        return "RegistryFileCodec[" + this.a + " " + this.b + "]";
    }
}

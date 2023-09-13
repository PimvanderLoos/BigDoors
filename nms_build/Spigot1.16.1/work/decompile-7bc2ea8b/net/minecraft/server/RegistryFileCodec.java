package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;

public final class RegistryFileCodec<E> implements Codec<Supplier<E>> {

    private final ResourceKey<IRegistry<E>> a;
    private final MapCodec<E> b;

    public static <E> RegistryFileCodec<E> a(ResourceKey<IRegistry<E>> resourcekey, MapCodec<E> mapcodec) {
        return new RegistryFileCodec<>(resourcekey, mapcodec);
    }

    private RegistryFileCodec(ResourceKey<IRegistry<E>> resourcekey, MapCodec<E> mapcodec) {
        this.a = resourcekey;
        this.b = mapcodec;
    }

    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicops, T t0) {
        return dynamicops instanceof RegistryWriteOps ? ((RegistryWriteOps) dynamicops).a(supplier.get(), t0, this.a, this.b) : this.b.codec().encode(supplier.get(), dynamicops, t0);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        return dynamicops instanceof RegistryReadOps ? ((RegistryReadOps) dynamicops).a(t0, this.a, this.b) : this.b.codec().decode(dynamicops, t0).map((pair) -> {
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

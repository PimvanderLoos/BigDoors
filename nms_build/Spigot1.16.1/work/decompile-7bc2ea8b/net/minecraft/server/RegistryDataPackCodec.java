package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;

public final class RegistryDataPackCodec<E> implements Codec<RegistryMaterials<E>> {

    private final Codec<RegistryMaterials<E>> a;
    private final ResourceKey<IRegistry<E>> b;
    private final MapCodec<E> c;

    public static <E> RegistryDataPackCodec<E> a(ResourceKey<IRegistry<E>> resourcekey, Lifecycle lifecycle, MapCodec<E> mapcodec) {
        return new RegistryDataPackCodec<>(resourcekey, lifecycle, mapcodec);
    }

    private RegistryDataPackCodec(ResourceKey<IRegistry<E>> resourcekey, Lifecycle lifecycle, MapCodec<E> mapcodec) {
        this.a = RegistryMaterials.c(resourcekey, lifecycle, mapcodec);
        this.b = resourcekey;
        this.c = mapcodec;
    }

    public <T> DataResult<T> encode(RegistryMaterials<E> registrymaterials, DynamicOps<T> dynamicops, T t0) {
        return this.a.encode(registrymaterials, dynamicops, t0);
    }

    public <T> DataResult<Pair<RegistryMaterials<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        DataResult<Pair<RegistryMaterials<E>, T>> dataresult = this.a.decode(dynamicops, t0);

        return dynamicops instanceof RegistryReadOps ? dataresult.flatMap((pair) -> {
            return ((RegistryReadOps) dynamicops).a((RegistryMaterials) pair.getFirst(), this.b, this.c).map((registrymaterials) -> {
                return Pair.of(registrymaterials, pair.getSecond());
            });
        }) : dataresult;
    }

    public String toString() {
        return "RegistryDapaPackCodec[" + this.a + " " + this.b + " " + this.c + "]";
    }
}

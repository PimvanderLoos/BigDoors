package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;

public final class RegistryDataPackCodec<E> implements Codec<RegistryMaterials<E>> {

    private final Codec<RegistryMaterials<E>> a;
    private final ResourceKey<? extends IRegistry<E>> b;
    private final Codec<E> c;

    public static <E> RegistryDataPackCodec<E> a(ResourceKey<? extends IRegistry<E>> resourcekey, Lifecycle lifecycle, Codec<E> codec) {
        return new RegistryDataPackCodec<>(resourcekey, lifecycle, codec);
    }

    private RegistryDataPackCodec(ResourceKey<? extends IRegistry<E>> resourcekey, Lifecycle lifecycle, Codec<E> codec) {
        this.a = RegistryMaterials.c(resourcekey, lifecycle, codec);
        this.b = resourcekey;
        this.c = codec;
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
        return "RegistryDataPackCodec[" + this.a + " " + this.b + " " + this.c + "]";
    }
}

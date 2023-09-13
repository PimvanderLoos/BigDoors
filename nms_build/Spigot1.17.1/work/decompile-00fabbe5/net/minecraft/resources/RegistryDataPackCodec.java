package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;

public final class RegistryDataPackCodec<E> implements Codec<RegistryMaterials<E>> {

    private final Codec<RegistryMaterials<E>> directCodec;
    private final ResourceKey<? extends IRegistry<E>> registryKey;
    private final Codec<E> elementCodec;

    public static <E> RegistryDataPackCodec<E> a(ResourceKey<? extends IRegistry<E>> resourcekey, Lifecycle lifecycle, Codec<E> codec) {
        return new RegistryDataPackCodec<>(resourcekey, lifecycle, codec);
    }

    private RegistryDataPackCodec(ResourceKey<? extends IRegistry<E>> resourcekey, Lifecycle lifecycle, Codec<E> codec) {
        this.directCodec = RegistryMaterials.c(resourcekey, lifecycle, codec);
        this.registryKey = resourcekey;
        this.elementCodec = codec;
    }

    public <T> DataResult<T> encode(RegistryMaterials<E> registrymaterials, DynamicOps<T> dynamicops, T t0) {
        return this.directCodec.encode(registrymaterials, dynamicops, t0);
    }

    public <T> DataResult<Pair<RegistryMaterials<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        DataResult<Pair<RegistryMaterials<E>, T>> dataresult = this.directCodec.decode(dynamicops, t0);

        return dynamicops instanceof RegistryReadOps ? dataresult.flatMap((pair) -> {
            return ((RegistryReadOps) dynamicops).a((RegistryMaterials) pair.getFirst(), this.registryKey, this.elementCodec).map((registrymaterials) -> {
                return Pair.of(registrymaterials, pair.getSecond());
            });
        }) : dataresult;
    }

    public String toString() {
        return "RegistryDataPackCodec[" + this.directCodec + " " + this.registryKey + " " + this.elementCodec + "]";
    }
}

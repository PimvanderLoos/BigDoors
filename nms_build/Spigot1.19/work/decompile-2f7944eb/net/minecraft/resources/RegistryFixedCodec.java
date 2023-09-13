package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;

public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {

    private final ResourceKey<? extends IRegistry<E>> registryKey;

    public static <E> RegistryFixedCodec<E> create(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return new RegistryFixedCodec<>(resourcekey);
    }

    private RegistryFixedCodec(ResourceKey<? extends IRegistry<E>> resourcekey) {
        this.registryKey = resourcekey;
    }

    public <T> DataResult<T> encode(Holder<E> holder, DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<?> registryops = (RegistryOps) dynamicops;
            Optional<? extends IRegistry<E>> optional = registryops.registry(this.registryKey);

            if (optional.isPresent()) {
                if (!holder.isValidInRegistry((IRegistry) optional.get())) {
                    return DataResult.error("Element " + holder + " is not valid in current registry set");
                }

                return (DataResult) holder.unwrap().map((resourcekey) -> {
                    return MinecraftKey.CODEC.encode(resourcekey.location(), dynamicops, t0);
                }, (object) -> {
                    return DataResult.error("Elements from registry " + this.registryKey + " can't be serialized to a value");
                });
            }
        }

        return DataResult.error("Can't access registry " + this.registryKey);
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<?> registryops = (RegistryOps) dynamicops;
            Optional<? extends IRegistry<E>> optional = registryops.registry(this.registryKey);

            if (optional.isPresent()) {
                return MinecraftKey.CODEC.decode(dynamicops, t0).flatMap((pair) -> {
                    MinecraftKey minecraftkey = (MinecraftKey) pair.getFirst();
                    DataResult<Holder<E>> dataresult = ((IRegistry) optional.get()).getOrCreateHolder(ResourceKey.create(this.registryKey, minecraftkey));

                    return dataresult.map((holder) -> {
                        return Pair.of(holder, pair.getSecond());
                    }).setLifecycle(Lifecycle.stable());
                });
            }
        }

        return DataResult.error("Can't access registry " + this.registryKey);
    }

    public String toString() {
        return "RegistryFixedCodec[" + this.registryKey + "]";
    }
}

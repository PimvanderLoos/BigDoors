package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
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
            Optional<HolderOwner<E>> optional = registryops.owner(this.registryKey);

            if (optional.isPresent()) {
                if (!holder.canSerializeIn((HolderOwner) optional.get())) {
                    return DataResult.error(() -> {
                        return "Element " + holder + " is not valid in current registry set";
                    });
                }

                return (DataResult) holder.unwrap().map((resourcekey) -> {
                    return MinecraftKey.CODEC.encode(resourcekey.location(), dynamicops, t0);
                }, (object) -> {
                    return DataResult.error(() -> {
                        return "Elements from registry " + this.registryKey + " can't be serialized to a value";
                    });
                });
            }
        }

        return DataResult.error(() -> {
            return "Can't access registry " + this.registryKey;
        });
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<?> registryops = (RegistryOps) dynamicops;
            Optional<HolderGetter<E>> optional = registryops.getter(this.registryKey);

            if (optional.isPresent()) {
                return MinecraftKey.CODEC.decode(dynamicops, t0).flatMap((pair) -> {
                    MinecraftKey minecraftkey = (MinecraftKey) pair.getFirst();

                    return ((DataResult) ((HolderGetter) optional.get()).get(ResourceKey.create(this.registryKey, minecraftkey)).map(DataResult::success).orElseGet(() -> {
                        return DataResult.error(() -> {
                            return "Failed to get element " + minecraftkey;
                        });
                    })).map((holder_c) -> {
                        return Pair.of(holder_c, pair.getSecond());
                    }).setLifecycle(Lifecycle.stable());
                });
            }
        }

        return DataResult.error(() -> {
            return "Can't access registry " + this.registryKey;
        });
    }

    public String toString() {
        return "RegistryFixedCodec[" + this.registryKey + "]";
    }
}

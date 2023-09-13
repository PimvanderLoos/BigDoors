package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;

public final class RegistryFileCodec<E> implements Codec<Holder<E>> {

    private final ResourceKey<? extends IRegistry<E>> registryKey;
    private final Codec<E> elementCodec;
    private final boolean allowInline;

    public static <E> RegistryFileCodec<E> create(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        return create(resourcekey, codec, true);
    }

    public static <E> RegistryFileCodec<E> create(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        return new RegistryFileCodec<>(resourcekey, codec, flag);
    }

    private RegistryFileCodec(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        this.registryKey = resourcekey;
        this.elementCodec = codec;
        this.allowInline = flag;
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
                    return this.elementCodec.encode(object, dynamicops, t0);
                });
            }
        }

        return this.elementCodec.encode(holder.value(), dynamicops, t0);
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<?> registryops = (RegistryOps) dynamicops;
            Optional<? extends IRegistry<E>> optional = registryops.registry(this.registryKey);

            if (optional.isEmpty()) {
                return DataResult.error("Registry does not exist: " + this.registryKey);
            } else {
                IRegistry<E> iregistry = (IRegistry) optional.get();
                DataResult<Pair<MinecraftKey, T>> dataresult = MinecraftKey.CODEC.decode(dynamicops, t0);

                if (dataresult.result().isEmpty()) {
                    return !this.allowInline ? DataResult.error("Inline definitions not allowed here") : this.elementCodec.decode(dynamicops, t0).map((pair) -> {
                        return pair.mapFirst(Holder::direct);
                    });
                } else {
                    Pair<MinecraftKey, T> pair = (Pair) dataresult.result().get();
                    ResourceKey<E> resourcekey = ResourceKey.create(this.registryKey, (MinecraftKey) pair.getFirst());
                    Optional<RegistryLoader.a> optional1 = registryops.registryLoader();

                    if (optional1.isPresent()) {
                        return ((RegistryLoader.a) optional1.get()).overrideElementFromResources(this.registryKey, this.elementCodec, resourcekey, registryops.getAsJson()).map((holder) -> {
                            return Pair.of(holder, pair.getSecond());
                        });
                    } else {
                        DataResult<Holder<E>> dataresult1 = iregistry.getOrCreateHolder(resourcekey);

                        return dataresult1.map((holder) -> {
                            return Pair.of(holder, pair.getSecond());
                        }).setLifecycle(Lifecycle.stable());
                    }
                }
            }
        } else {
            return this.elementCodec.decode(dynamicops, t0).map((pair1) -> {
                return pair1.mapFirst(Holder::direct);
            });
        }
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryKey + " " + this.elementCodec + "]";
    }
}

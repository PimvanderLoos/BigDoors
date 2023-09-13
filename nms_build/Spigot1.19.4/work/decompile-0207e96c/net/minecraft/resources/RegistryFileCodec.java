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
                    return this.elementCodec.encode(object, dynamicops, t0);
                });
            }
        }

        return this.elementCodec.encode(holder.value(), dynamicops, t0);
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<?> registryops = (RegistryOps) dynamicops;
            Optional<HolderGetter<E>> optional = registryops.getter(this.registryKey);

            if (optional.isEmpty()) {
                return DataResult.error(() -> {
                    return "Registry does not exist: " + this.registryKey;
                });
            } else {
                HolderGetter<E> holdergetter = (HolderGetter) optional.get();
                DataResult<Pair<MinecraftKey, T>> dataresult = MinecraftKey.CODEC.decode(dynamicops, t0);

                if (dataresult.result().isEmpty()) {
                    return !this.allowInline ? DataResult.error(() -> {
                        return "Inline definitions not allowed here";
                    }) : this.elementCodec.decode(dynamicops, t0).map((pair) -> {
                        return pair.mapFirst(Holder::direct);
                    });
                } else {
                    Pair<MinecraftKey, T> pair = (Pair) dataresult.result().get();
                    ResourceKey<E> resourcekey = ResourceKey.create(this.registryKey, (MinecraftKey) pair.getFirst());

                    return ((DataResult) holdergetter.get(resourcekey).map(DataResult::success).orElseGet(() -> {
                        return DataResult.error(() -> {
                            return "Failed to get element " + resourcekey;
                        });
                    })).map((holder_c) -> {
                        return Pair.of(holder_c, pair.getSecond());
                    }).setLifecycle(Lifecycle.stable());
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

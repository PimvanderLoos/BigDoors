package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public class ResourceKey<T> {

    private static final Map<String, ResourceKey<?>> VALUES = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final MinecraftKey registryName;
    private final MinecraftKey location;

    public static <T> Codec<ResourceKey<T>> codec(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return MinecraftKey.CODEC.xmap((minecraftkey) -> {
            return create(resourcekey, minecraftkey);
        }, ResourceKey::location);
    }

    public static <T> ResourceKey<T> create(ResourceKey<? extends IRegistry<T>> resourcekey, MinecraftKey minecraftkey) {
        return create(resourcekey.location, minecraftkey);
    }

    public static <T> ResourceKey<IRegistry<T>> createRegistryKey(MinecraftKey minecraftkey) {
        return create(IRegistry.ROOT_REGISTRY_NAME, minecraftkey);
    }

    private static <T> ResourceKey<T> create(MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        String s = (minecraftkey + ":" + minecraftkey1).intern();

        return (ResourceKey) ResourceKey.VALUES.computeIfAbsent(s, (s1) -> {
            return new ResourceKey<>(minecraftkey, minecraftkey1);
        });
    }

    private ResourceKey(MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        this.registryName = minecraftkey;
        this.location = minecraftkey1;
    }

    public String toString() {
        return "ResourceKey[" + this.registryName + " / " + this.location + "]";
    }

    public boolean isFor(ResourceKey<? extends IRegistry<?>> resourcekey) {
        return this.registryName.equals(resourcekey.location());
    }

    public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return this.isFor(resourcekey) ? Optional.of(this) : Optional.empty();
    }

    public MinecraftKey location() {
        return this.location;
    }

    public static <T> Function<MinecraftKey, ResourceKey<T>> elementKey(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (minecraftkey) -> {
            return create(resourcekey, minecraftkey);
        };
    }
}

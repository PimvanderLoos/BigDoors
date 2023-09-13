package net.minecraft.resources;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public class ResourceKey<T> {

    private static final Map<String, ResourceKey<?>> VALUES = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final MinecraftKey registryName;
    private final MinecraftKey location;

    public static <T> ResourceKey<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, MinecraftKey minecraftkey) {
        return a(resourcekey.location, minecraftkey);
    }

    public static <T> ResourceKey<IRegistry<T>> a(MinecraftKey minecraftkey) {
        return a(IRegistry.ROOT_REGISTRY_NAME, minecraftkey);
    }

    private static <T> ResourceKey<T> a(MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
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

    public boolean a(ResourceKey<? extends IRegistry<?>> resourcekey) {
        return this.registryName.equals(resourcekey.a());
    }

    public MinecraftKey a() {
        return this.location;
    }

    public static <T> Function<MinecraftKey, ResourceKey<T>> b(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (minecraftkey) -> {
            return a(resourcekey, minecraftkey);
        };
    }
}

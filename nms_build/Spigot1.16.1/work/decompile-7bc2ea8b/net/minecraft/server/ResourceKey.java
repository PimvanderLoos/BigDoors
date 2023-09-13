package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class ResourceKey<T> {

    private static final Map<String, ResourceKey<?>> a = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final MinecraftKey b;
    private final MinecraftKey c;

    public static <T> ResourceKey<T> a(ResourceKey<IRegistry<T>> resourcekey, MinecraftKey minecraftkey) {
        return a(resourcekey.c, minecraftkey);
    }

    public static <T> ResourceKey<IRegistry<T>> a(MinecraftKey minecraftkey) {
        return a(IRegistry.f, minecraftkey);
    }

    private static <T> ResourceKey<T> a(MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        String s = (minecraftkey + ":" + minecraftkey1).intern();

        return (ResourceKey) ResourceKey.a.computeIfAbsent(s, (s1) -> {
            return new ResourceKey<>(minecraftkey, minecraftkey1);
        });
    }

    private ResourceKey(MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        this.b = minecraftkey;
        this.c = minecraftkey1;
    }

    public String toString() {
        return "ResourceKey[" + this.b + " / " + this.c + ']';
    }

    public MinecraftKey a() {
        return this.c;
    }

    public static <T> Function<MinecraftKey, ResourceKey<T>> a(ResourceKey<IRegistry<T>> resourcekey) {
        return (minecraftkey) -> {
            return a(resourcekey, minecraftkey);
        };
    }
}

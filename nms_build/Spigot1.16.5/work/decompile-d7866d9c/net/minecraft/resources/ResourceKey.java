package net.minecraft.resources;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public class ResourceKey<T> {

    private static final Map<String, ResourceKey<?>> a = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final MinecraftKey b;
    private final MinecraftKey c;

    public static <T> ResourceKey<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, MinecraftKey minecraftkey) {
        return a(resourcekey.c, minecraftkey);
    }

    public static <T> ResourceKey<IRegistry<T>> a(MinecraftKey minecraftkey) {
        return a(IRegistry.d, minecraftkey);
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

    public boolean a(ResourceKey<? extends IRegistry<?>> resourcekey) {
        return this.b.equals(resourcekey.a());
    }

    public MinecraftKey a() {
        return this.c;
    }

    public static <T> Function<MinecraftKey, ResourceKey<T>> b(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (minecraftkey) -> {
            return a(resourcekey, minecraftkey);
        };
    }
}

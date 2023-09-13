package net.minecraft.server;

import com.mojang.serialization.Lifecycle;

public abstract class IRegistryWritable<T> extends IRegistry<T> {

    public IRegistryWritable(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
    }

    public abstract <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0);

    public abstract <V extends T> V a(ResourceKey<T> resourcekey, V v0);

    public abstract void d(ResourceKey<T> resourcekey);
}

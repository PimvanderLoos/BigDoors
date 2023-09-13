package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceKey;

public abstract class IRegistryWritable<T> extends IRegistry<T> {

    public IRegistryWritable(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
    }

    public abstract <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle);

    public abstract <V extends T> V a(ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle);

    public abstract <V extends T> V a(OptionalInt optionalint, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle);
}

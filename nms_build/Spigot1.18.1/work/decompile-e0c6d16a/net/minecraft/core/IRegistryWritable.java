package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceKey;

public abstract class IRegistryWritable<T> extends IRegistry<T> {

    public IRegistryWritable(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
    }

    public abstract <V extends T> V registerMapping(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle);

    public abstract <V extends T> V register(ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle);

    public abstract <V extends T> V registerOrOverride(OptionalInt optionalint, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle);

    public abstract boolean isEmpty();
}

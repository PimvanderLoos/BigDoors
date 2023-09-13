package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceKey;

public abstract class IRegistryWritable<T> extends IRegistry<T> {

    public IRegistryWritable(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
    }

    public abstract Holder<T> registerMapping(int i, ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle);

    public abstract Holder<T> register(ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle);

    public abstract Holder<T> registerOrOverride(OptionalInt optionalint, ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle);

    public abstract boolean isEmpty();
}

package net.minecraft.data.worldgen;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.ResourceKey;

public interface BootstapContext<T> {

    Holder.c<T> register(ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle);

    default Holder.c<T> register(ResourceKey<T> resourcekey, T t0) {
        return this.register(resourcekey, t0, Lifecycle.stable());
    }

    <S> HolderGetter<S> lookup(ResourceKey<? extends IRegistry<? extends S>> resourcekey);
}

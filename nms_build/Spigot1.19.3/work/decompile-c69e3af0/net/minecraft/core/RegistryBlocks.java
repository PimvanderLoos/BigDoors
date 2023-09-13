package net.minecraft.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;

public interface RegistryBlocks<T> extends IRegistry<T> {

    @Nonnull
    @Override
    MinecraftKey getKey(T t0);

    @Nonnull
    @Override
    T get(@Nullable MinecraftKey minecraftkey);

    @Nonnull
    @Override
    T byId(int i);

    MinecraftKey getDefaultKey();
}

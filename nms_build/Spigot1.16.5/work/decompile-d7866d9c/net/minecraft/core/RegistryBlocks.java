package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class RegistryBlocks<T> extends RegistryMaterials<T> {

    private final MinecraftKey bf;
    private T bg;

    public RegistryBlocks(String s, ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
        this.bf = new MinecraftKey(s);
    }

    @Override
    public <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
        if (this.bf.equals(resourcekey.a())) {
            this.bg = v0;
        }

        return super.a(i, resourcekey, v0, lifecycle);
    }

    @Override
    public int a(@Nullable T t0) {
        int i = super.a(t0);

        return i == -1 ? super.a(this.bg) : i;
    }

    @Nonnull
    @Override
    public MinecraftKey getKey(T t0) {
        MinecraftKey minecraftkey = super.getKey(t0);

        return minecraftkey == null ? this.bf : minecraftkey;
    }

    @Nonnull
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        T t0 = super.get(minecraftkey);

        return t0 == null ? this.bg : t0;
    }

    @Override
    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(super.get(minecraftkey));
    }

    @Nonnull
    @Override
    public T fromId(int i) {
        T t0 = super.fromId(i);

        return t0 == null ? this.bg : t0;
    }

    @Nonnull
    @Override
    public T a(Random random) {
        T t0 = super.a(random);

        return t0 == null ? this.bg : t0;
    }

    public MinecraftKey a() {
        return this.bf;
    }
}

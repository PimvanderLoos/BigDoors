package net.minecraft.server;

import com.mojang.serialization.Lifecycle;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RegistryBlocks<T> extends RegistryMaterials<T> {

    private final MinecraftKey bb;
    private T bc;

    public RegistryBlocks(String s, ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
        this.bb = new MinecraftKey(s);
    }

    @Override
    public <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0) {
        if (this.bb.equals(resourcekey.a())) {
            this.bc = v0;
        }

        return super.a(i, resourcekey, v0);
    }

    @Override
    public int a(@Nullable T t0) {
        int i = super.a(t0);

        return i == -1 ? super.a(this.bc) : i;
    }

    @Nonnull
    @Override
    public MinecraftKey getKey(T t0) {
        MinecraftKey minecraftkey = super.getKey(t0);

        return minecraftkey == null ? this.bb : minecraftkey;
    }

    @Nonnull
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        T t0 = super.get(minecraftkey);

        return t0 == null ? this.bc : t0;
    }

    @Nonnull
    @Override
    public T fromId(int i) {
        T t0 = super.fromId(i);

        return t0 == null ? this.bc : t0;
    }

    @Nonnull
    @Override
    public T a(Random random) {
        T t0 = super.a(random);

        return t0 == null ? this.bc : t0;
    }

    public MinecraftKey a() {
        return this.bb;
    }
}

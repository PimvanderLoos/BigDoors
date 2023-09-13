package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

public class RegistryBlocks<T> extends RegistryMaterials<T> {

    private final MinecraftKey defaultKey;
    private Holder<T> defaultValue;

    public RegistryBlocks(String s, ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, @Nullable Function<T, Holder.c<T>> function) {
        super(resourcekey, lifecycle, function);
        this.defaultKey = new MinecraftKey(s);
    }

    @Override
    public Holder<T> registerMapping(int i, ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle) {
        Holder<T> holder = super.registerMapping(i, resourcekey, t0, lifecycle);

        if (this.defaultKey.equals(resourcekey.location())) {
            this.defaultValue = holder;
        }

        return holder;
    }

    @Override
    public int getId(@Nullable T t0) {
        int i = super.getId(t0);

        return i == -1 ? super.getId(this.defaultValue.value()) : i;
    }

    @Nonnull
    @Override
    public MinecraftKey getKey(T t0) {
        MinecraftKey minecraftkey = super.getKey(t0);

        return minecraftkey == null ? this.defaultKey : minecraftkey;
    }

    @Nonnull
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        T t0 = super.get(minecraftkey);

        return t0 == null ? this.defaultValue.value() : t0;
    }

    @Override
    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(super.get(minecraftkey));
    }

    @Nonnull
    @Override
    public T byId(int i) {
        T t0 = super.byId(i);

        return t0 == null ? this.defaultValue.value() : t0;
    }

    @Override
    public Optional<Holder<T>> getRandom(RandomSource randomsource) {
        return super.getRandom(randomsource).or(() -> {
            return Optional.of(this.defaultValue);
        });
    }

    public MinecraftKey getDefaultKey() {
        return this.defaultKey;
    }
}

package net.minecraft.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;

public class RegistryWriteOps<T> extends DynamicOpsWrapper<T> {

    private final IRegistryCustom registryAccess;

    public static <T> RegistryWriteOps<T> a(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        return new RegistryWriteOps<>(dynamicops, iregistrycustom);
    }

    private RegistryWriteOps(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        super(dynamicops);
        this.registryAccess = iregistrycustom;
    }

    protected <E> DataResult<T> a(E e0, T t0, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        Optional<IRegistryWritable<E>> optional = this.registryAccess.a(resourcekey);

        if (optional.isPresent()) {
            IRegistryWritable<E> iregistrywritable = (IRegistryWritable) optional.get();
            Optional<ResourceKey<E>> optional1 = iregistrywritable.c(e0);

            if (optional1.isPresent()) {
                ResourceKey<E> resourcekey1 = (ResourceKey) optional1.get();

                return MinecraftKey.CODEC.encode(resourcekey1.a(), this.delegate, t0);
            }
        }

        return codec.encode(e0, this, t0);
    }
}

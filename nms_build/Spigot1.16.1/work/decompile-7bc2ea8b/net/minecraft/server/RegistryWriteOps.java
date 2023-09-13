package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;

public class RegistryWriteOps<T> extends DynamicOpsWrapper<T> {

    private final IRegistryCustom b;

    public static <T> RegistryWriteOps<T> a(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        return new RegistryWriteOps<>(dynamicops, iregistrycustom);
    }

    private RegistryWriteOps(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        super(dynamicops);
        this.b = iregistrycustom;
    }

    protected <E> DataResult<T> a(E e0, T t0, ResourceKey<IRegistry<E>> resourcekey, MapCodec<E> mapcodec) {
        Optional<IRegistryWritable<E>> optional = this.b.a(resourcekey);

        if (optional.isPresent()) {
            IRegistryWritable<E> iregistrywritable = (IRegistryWritable) optional.get();
            Optional<ResourceKey<E>> optional1 = iregistrywritable.c(e0);

            if (optional1.isPresent()) {
                ResourceKey<E> resourcekey1 = (ResourceKey) optional1.get();

                if (iregistrywritable.c(resourcekey1)) {
                    return Codecs.a(resourcekey, mapcodec).codec().encode(Pair.of(resourcekey1, e0), this.a, t0);
                }

                return MinecraftKey.a.encode(resourcekey1.a(), this.a, t0);
            }
        }

        return mapcodec.codec().encode(e0, this.a, t0);
    }
}

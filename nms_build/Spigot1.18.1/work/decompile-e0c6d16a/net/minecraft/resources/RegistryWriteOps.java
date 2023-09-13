package net.minecraft.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;

public class RegistryWriteOps<T> extends DynamicOpsWrapper<T> {

    private final IRegistryCustom registryAccess;

    public static <T> RegistryWriteOps<T> create(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        return new RegistryWriteOps<>(dynamicops, iregistrycustom);
    }

    private RegistryWriteOps(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        super(dynamicops);
        this.registryAccess = iregistrycustom;
    }

    protected <E> DataResult<T> encode(E e0, T t0, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        Optional<? extends IRegistry<E>> optional = this.registryAccess.ownedRegistry(resourcekey);

        if (optional.isPresent()) {
            IRegistry<E> iregistry = (IRegistry) optional.get();
            Optional<ResourceKey<E>> optional1 = iregistry.getResourceKey(e0);

            if (optional1.isPresent()) {
                ResourceKey<E> resourcekey1 = (ResourceKey) optional1.get();

                return MinecraftKey.CODEC.encode(resourcekey1.location(), this.delegate, t0);
            }
        }

        return codec.encode(e0, this, t0);
    }
}

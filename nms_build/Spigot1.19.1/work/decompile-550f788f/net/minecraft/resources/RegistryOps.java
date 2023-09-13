package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DynamicOpsWrapper<T> {

    private final Optional<RegistryLoader.a> loader;
    private final IRegistryCustom registryAccess;
    private final DynamicOps<JsonElement> asJson;

    public static <T> RegistryOps<T> create(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom) {
        return new RegistryOps<>(dynamicops, iregistrycustom, Optional.empty());
    }

    public static <T> RegistryOps<T> createAndLoad(DynamicOps<T> dynamicops, IRegistryCustom.e iregistrycustom_e, IResourceManager iresourcemanager) {
        return createAndLoad(dynamicops, iregistrycustom_e, RegistryResourceAccess.forResourceManager(iresourcemanager));
    }

    public static <T> RegistryOps<T> createAndLoad(DynamicOps<T> dynamicops, IRegistryCustom.e iregistrycustom_e, RegistryResourceAccess registryresourceaccess) {
        RegistryLoader registryloader = new RegistryLoader(registryresourceaccess);
        RegistryOps<T> registryops = new RegistryOps<>(dynamicops, iregistrycustom_e, Optional.of(registryloader.bind(iregistrycustom_e)));

        IRegistryCustom.load(iregistrycustom_e, registryops.getAsJson(), registryloader);
        return registryops;
    }

    private RegistryOps(DynamicOps<T> dynamicops, IRegistryCustom iregistrycustom, Optional<RegistryLoader.a> optional) {
        super(dynamicops);
        this.loader = optional;
        this.registryAccess = iregistrycustom;
        this.asJson = dynamicops == JsonOps.INSTANCE ? this : new RegistryOps<>(JsonOps.INSTANCE, iregistrycustom, optional);
    }

    public <E> Optional<? extends IRegistry<E>> registry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return this.registryAccess.registry(resourcekey);
    }

    public Optional<RegistryLoader.a> registryLoader() {
        return this.loader;
    }

    public DynamicOps<JsonElement> getAsJson() {
        return this.asJson;
    }

    public static <E> MapCodec<IRegistry<E>> retrieveRegistry(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return ExtraCodecs.retrieveContext((dynamicops) -> {
            if (dynamicops instanceof RegistryOps) {
                RegistryOps<?> registryops = (RegistryOps) dynamicops;

                return (DataResult) registryops.registry(resourcekey).map((iregistry) -> {
                    return DataResult.success(iregistry, iregistry.elementsLifecycle());
                }).orElseGet(() -> {
                    return DataResult.error("Unknown registry: " + resourcekey);
                });
            } else {
                return DataResult.error("Not a registry ops");
            }
        });
    }
}

package net.minecraft.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.server.packs.resources.IResourceManager;

public class RegistryReadOps<T> extends DynamicOpsWrapper<T> {

    private final RegistryResourceAccess resources;
    private final IRegistryCustom registryAccess;
    private final Map<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> readCache;
    private final RegistryReadOps<JsonElement> jsonOps;

    public static <T> RegistryReadOps<T> createAndLoad(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom iregistrycustom) {
        return createAndLoad(dynamicops, RegistryResourceAccess.forResourceManager(iresourcemanager), iregistrycustom);
    }

    public static <T> RegistryReadOps<T> createAndLoad(DynamicOps<T> dynamicops, RegistryResourceAccess registryresourceaccess, IRegistryCustom iregistrycustom) {
        RegistryReadOps<T> registryreadops = new RegistryReadOps<>(dynamicops, registryresourceaccess, iregistrycustom, Maps.newIdentityHashMap());

        IRegistryCustom.load(iregistrycustom, registryreadops);
        return registryreadops;
    }

    public static <T> RegistryReadOps<T> create(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom iregistrycustom) {
        return create(dynamicops, RegistryResourceAccess.forResourceManager(iresourcemanager), iregistrycustom);
    }

    public static <T> RegistryReadOps<T> create(DynamicOps<T> dynamicops, RegistryResourceAccess registryresourceaccess, IRegistryCustom iregistrycustom) {
        return new RegistryReadOps<>(dynamicops, registryresourceaccess, iregistrycustom, Maps.newIdentityHashMap());
    }

    private RegistryReadOps(DynamicOps<T> dynamicops, RegistryResourceAccess registryresourceaccess, IRegistryCustom iregistrycustom, IdentityHashMap<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> identityhashmap) {
        super(dynamicops);
        this.resources = registryresourceaccess;
        this.registryAccess = iregistrycustom;
        this.readCache = identityhashmap;
        this.jsonOps = dynamicops == JsonOps.INSTANCE ? this : new RegistryReadOps<>(JsonOps.INSTANCE, registryresourceaccess, iregistrycustom, identityhashmap);
    }

    protected <E> DataResult<Pair<Supplier<E>, T>> decodeElement(T t0, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        Optional<IRegistryWritable<E>> optional = this.registryAccess.ownedRegistry(resourcekey);

        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + resourcekey);
        } else {
            IRegistryWritable<E> iregistrywritable = (IRegistryWritable) optional.get();
            DataResult<Pair<MinecraftKey, T>> dataresult = MinecraftKey.CODEC.decode(this.delegate, t0);

            if (!dataresult.result().isPresent()) {
                return !flag ? DataResult.error("Inline definitions not allowed here") : codec.decode(this, t0).map((pair) -> {
                    return pair.mapFirst((object) -> {
                        return () -> {
                            return object;
                        };
                    });
                });
            } else {
                Pair<MinecraftKey, T> pair = (Pair) dataresult.result().get();
                ResourceKey<E> resourcekey1 = ResourceKey.create(resourcekey, (MinecraftKey) pair.getFirst());

                return this.readAndRegisterElement(resourcekey, iregistrywritable, codec, resourcekey1).map((supplier) -> {
                    return Pair.of(supplier, pair.getSecond());
                });
            }
        }
    }

    public <E> DataResult<RegistryMaterials<E>> decodeElements(RegistryMaterials<E> registrymaterials, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        Collection<ResourceKey<E>> collection = this.resources.listResources(resourcekey);
        DataResult<RegistryMaterials<E>> dataresult = DataResult.success(registrymaterials, Lifecycle.stable());

        ResourceKey resourcekey1;

        for (Iterator iterator = collection.iterator(); iterator.hasNext();dataresult = dataresult.flatMap((registrymaterials1) -> {
            return this.readAndRegisterElement(resourcekey, registrymaterials1, codec, resourcekey1).map((supplier) -> {
                return registrymaterials1;
            });
        })) {
            resourcekey1 = (ResourceKey) iterator.next();
        }

        return dataresult.setPartial(registrymaterials);
    }

    private <E> DataResult<Supplier<E>> readAndRegisterElement(ResourceKey<? extends IRegistry<E>> resourcekey, IRegistryWritable<E> iregistrywritable, Codec<E> codec, ResourceKey<E> resourcekey1) {
        RegistryReadOps.a<E> registryreadops_a = this.readCache(resourcekey);
        DataResult<Supplier<E>> dataresult = (DataResult) registryreadops_a.values.get(resourcekey1);

        if (dataresult != null) {
            return dataresult;
        } else {
            registryreadops_a.values.put(resourcekey1, DataResult.success(createPlaceholderGetter(iregistrywritable, resourcekey1)));
            Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> optional = this.resources.parseElement(this.jsonOps, resourcekey, resourcekey1, codec);
            DataResult dataresult1;

            if (optional.isEmpty()) {
                if (iregistrywritable.containsKey(resourcekey1)) {
                    dataresult1 = DataResult.success(createRegistryGetter(iregistrywritable, resourcekey1), Lifecycle.stable());
                } else {
                    dataresult1 = DataResult.error("Missing referenced custom/removed registry entry for registry " + resourcekey + " named " + resourcekey1.location());
                }
            } else {
                DataResult<RegistryResourceAccess.ParsedEntry<E>> dataresult2 = (DataResult) optional.get();
                Optional<RegistryResourceAccess.ParsedEntry<E>> optional1 = dataresult2.result();

                if (optional1.isPresent()) {
                    RegistryResourceAccess.ParsedEntry<E> registryresourceaccess_parsedentry = (RegistryResourceAccess.ParsedEntry) optional1.get();

                    iregistrywritable.registerOrOverride(registryresourceaccess_parsedentry.fixedId(), resourcekey1, registryresourceaccess_parsedentry.value(), dataresult2.lifecycle());
                }

                dataresult1 = dataresult2.map((registryresourceaccess_parsedentry1) -> {
                    return createRegistryGetter(iregistrywritable, resourcekey1);
                });
            }

            registryreadops_a.values.put(resourcekey1, dataresult1);
            return dataresult1;
        }
    }

    private static <E> Supplier<E> createPlaceholderGetter(IRegistryWritable<E> iregistrywritable, ResourceKey<E> resourcekey) {
        return Suppliers.memoize(() -> {
            E e0 = iregistrywritable.get(resourcekey);

            if (e0 == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + resourcekey);
            } else {
                return e0;
            }
        });
    }

    private static <E> Supplier<E> createRegistryGetter(final IRegistry<E> iregistry, final ResourceKey<E> resourcekey) {
        return new Supplier<E>() {
            public E get() {
                return iregistry.get(resourcekey);
            }

            public String toString() {
                return resourcekey.toString();
            }
        };
    }

    private <E> RegistryReadOps.a<E> readCache(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (RegistryReadOps.a) this.readCache.computeIfAbsent(resourcekey, (resourcekey1) -> {
            return new RegistryReadOps.a<>();
        });
    }

    protected <E> DataResult<IRegistry<E>> registry(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (DataResult) this.registryAccess.ownedRegistry(resourcekey).map((iregistrywritable) -> {
            return DataResult.success(iregistrywritable, iregistrywritable.elementsLifecycle());
        }).orElseGet(() -> {
            return DataResult.error("Unknown registry: " + resourcekey);
        });
    }

    private static final class a<E> {

        final Map<ResourceKey<E>, DataResult<Supplier<E>>> values = Maps.newIdentityHashMap();

        a() {}
    }
}

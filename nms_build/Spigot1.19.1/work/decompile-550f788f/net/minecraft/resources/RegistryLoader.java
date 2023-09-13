package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;

public class RegistryLoader {

    private final RegistryResourceAccess resources;
    private final Map<ResourceKey<? extends IRegistry<?>>, RegistryLoader.b<?>> readCache = new IdentityHashMap();

    RegistryLoader(RegistryResourceAccess registryresourceaccess) {
        this.resources = registryresourceaccess;
    }

    public <E> DataResult<? extends IRegistry<E>> overrideRegistryFromResources(IRegistryWritable<E> iregistrywritable, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, DynamicOps<JsonElement> dynamicops) {
        Map<ResourceKey<E>, RegistryResourceAccess.a<E>> map = this.resources.listResources(resourcekey);
        DataResult<IRegistryWritable<E>> dataresult = DataResult.success(iregistrywritable, Lifecycle.stable());

        Entry entry;

        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();dataresult = dataresult.flatMap((iregistrywritable1) -> {
            return this.overrideElementFromResources(iregistrywritable1, resourcekey, codec, (ResourceKey) entry.getKey(), Optional.of((RegistryResourceAccess.a) entry.getValue()), dynamicops).map((holder) -> {
                return iregistrywritable1;
            });
        })) {
            entry = (Entry) iterator.next();
        }

        return dataresult.setPartial(iregistrywritable);
    }

    <E> DataResult<Holder<E>> overrideElementFromResources(IRegistryWritable<E> iregistrywritable, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, ResourceKey<E> resourcekey1, DynamicOps<JsonElement> dynamicops) {
        Optional<RegistryResourceAccess.a<E>> optional = this.resources.getResource(resourcekey1);

        return this.overrideElementFromResources(iregistrywritable, resourcekey, codec, resourcekey1, optional, dynamicops);
    }

    private <E> DataResult<Holder<E>> overrideElementFromResources(IRegistryWritable<E> iregistrywritable, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, ResourceKey<E> resourcekey1, Optional<RegistryResourceAccess.a<E>> optional, DynamicOps<JsonElement> dynamicops) {
        RegistryLoader.b<E> registryloader_b = this.readCache(resourcekey);
        DataResult<Holder<E>> dataresult = (DataResult) registryloader_b.values.get(resourcekey1);

        if (dataresult != null) {
            return dataresult;
        } else {
            Holder<E> holder = iregistrywritable.getOrCreateHolderOrThrow(resourcekey1);

            registryloader_b.values.put(resourcekey1, DataResult.success(holder));
            DataResult dataresult1;

            if (optional.isEmpty()) {
                if (iregistrywritable.containsKey(resourcekey1)) {
                    dataresult1 = DataResult.success(holder, Lifecycle.stable());
                } else {
                    dataresult1 = DataResult.error("Missing referenced custom/removed registry entry for registry " + resourcekey + " named " + resourcekey1.location());
                }
            } else {
                DataResult<RegistryResourceAccess.ParsedEntry<E>> dataresult2 = ((RegistryResourceAccess.a) optional.get()).parseElement(dynamicops, codec);
                Optional<RegistryResourceAccess.ParsedEntry<E>> optional1 = dataresult2.result();

                if (optional1.isPresent()) {
                    RegistryResourceAccess.ParsedEntry<E> registryresourceaccess_parsedentry = (RegistryResourceAccess.ParsedEntry) optional1.get();

                    iregistrywritable.registerOrOverride(registryresourceaccess_parsedentry.fixedId(), resourcekey1, registryresourceaccess_parsedentry.value(), dataresult2.lifecycle());
                }

                dataresult1 = dataresult2.map((registryresourceaccess_parsedentry1) -> {
                    return holder;
                });
            }

            registryloader_b.values.put(resourcekey1, dataresult1);
            return dataresult1;
        }
    }

    private <E> RegistryLoader.b<E> readCache(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (RegistryLoader.b) this.readCache.computeIfAbsent(resourcekey, (resourcekey1) -> {
            return new RegistryLoader.b<>();
        });
    }

    public RegistryLoader.a bind(IRegistryCustom.e iregistrycustom_e) {
        return new RegistryLoader.a(iregistrycustom_e, this);
    }

    private static final class b<E> {

        final Map<ResourceKey<E>, DataResult<Holder<E>>> values = Maps.newIdentityHashMap();

        b() {}
    }

    public static record a(IRegistryCustom.e access, RegistryLoader loader) {

        public <E> DataResult<? extends IRegistry<E>> overrideRegistryFromResources(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, DynamicOps<JsonElement> dynamicops) {
            IRegistryWritable<E> iregistrywritable = this.access.ownedWritableRegistryOrThrow(resourcekey);

            return this.loader.overrideRegistryFromResources(iregistrywritable, resourcekey, codec, dynamicops);
        }

        public <E> DataResult<Holder<E>> overrideElementFromResources(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, ResourceKey<E> resourcekey1, DynamicOps<JsonElement> dynamicops) {
            IRegistryWritable<E> iregistrywritable = this.access.ownedWritableRegistryOrThrow(resourcekey);

            return this.loader.overrideElementFromResources(iregistrywritable, resourcekey, codec, resourcekey1, dynamicops);
        }
    }
}

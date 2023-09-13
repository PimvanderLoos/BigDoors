package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.IRegistry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DynamicOpsWrapper<T> {

    private final RegistryOps.b lookupProvider;

    private static RegistryOps.b memoizeLookup(final RegistryOps.b registryops_b) {
        return new RegistryOps.b() {
            private final Map<ResourceKey<? extends IRegistry<?>>, Optional<? extends RegistryOps.a<?>>> lookups = new HashMap();

            @Override
            public <T> Optional<RegistryOps.a<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
                Map map = this.lookups;
                RegistryOps.b registryops_b1 = registryops_b;

                Objects.requireNonNull(registryops_b);
                return (Optional) map.computeIfAbsent(resourcekey, registryops_b1::lookup);
            }
        };
    }

    public static <T> RegistryOps<T> create(DynamicOps<T> dynamicops, final HolderLookup.b holderlookup_b) {
        return create(dynamicops, memoizeLookup(new RegistryOps.b() {
            @Override
            public <E> Optional<RegistryOps.a<E>> lookup(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
                return holderlookup_b.lookup(resourcekey).map((holderlookup_c) -> {
                    return new RegistryOps.a<>(holderlookup_c, holderlookup_c, holderlookup_c.registryLifecycle());
                });
            }
        }));
    }

    public static <T> RegistryOps<T> create(DynamicOps<T> dynamicops, RegistryOps.b registryops_b) {
        return new RegistryOps<>(dynamicops, registryops_b);
    }

    private RegistryOps(DynamicOps<T> dynamicops, RegistryOps.b registryops_b) {
        super(dynamicops);
        this.lookupProvider = registryops_b;
    }

    public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return this.lookupProvider.lookup(resourcekey).map(RegistryOps.a::owner);
    }

    public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return this.lookupProvider.lookup(resourcekey).map(RegistryOps.a::getter);
    }

    public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends IRegistry<? extends E>> resourcekey) {
        return ExtraCodecs.retrieveContext((dynamicops) -> {
            if (dynamicops instanceof RegistryOps) {
                RegistryOps<?> registryops = (RegistryOps) dynamicops;

                return (DataResult) registryops.lookupProvider.lookup(resourcekey).map((registryops_a) -> {
                    return DataResult.success(registryops_a.getter(), registryops_a.elementsLifecycle());
                }).orElseGet(() -> {
                    return DataResult.error(() -> {
                        return "Unknown registry: " + resourcekey;
                    });
                });
            } else {
                return DataResult.error(() -> {
                    return "Not a registry ops";
                });
            }
        }).forGetter((object) -> {
            return null;
        });
    }

    public static <E, O> RecordCodecBuilder<O, Holder.c<E>> retrieveElement(ResourceKey<E> resourcekey) {
        ResourceKey<? extends IRegistry<E>> resourcekey1 = ResourceKey.createRegistryKey(resourcekey.registry());

        return ExtraCodecs.retrieveContext((dynamicops) -> {
            if (dynamicops instanceof RegistryOps) {
                RegistryOps<?> registryops = (RegistryOps) dynamicops;

                return (DataResult) registryops.lookupProvider.lookup(resourcekey1).flatMap((registryops_a) -> {
                    return registryops_a.getter().get(resourcekey);
                }).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error(() -> {
                        return "Can't find value: " + resourcekey;
                    });
                });
            } else {
                return DataResult.error(() -> {
                    return "Not a registry ops";
                });
            }
        }).forGetter((object) -> {
            return null;
        });
    }

    public interface b {

        <T> Optional<RegistryOps.a<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey);
    }

    public static record a<T> (HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {

    }
}

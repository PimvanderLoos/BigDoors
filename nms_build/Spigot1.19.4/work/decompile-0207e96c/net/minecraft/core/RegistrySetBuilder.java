package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class RegistrySetBuilder {

    private final List<RegistrySetBuilder.g<?>> entries = new ArrayList();

    public RegistrySetBuilder() {}

    static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.c<T> holderlookup_c) {
        return new RegistrySetBuilder.c<T>(holderlookup_c) {
            @Override
            public Optional<Holder.c<T>> get(ResourceKey<T> resourcekey) {
                return holderlookup_c.get(resourcekey);
            }
        };
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, RegistrySetBuilder.e<T> registrysetbuilder_e) {
        this.entries.add(new RegistrySetBuilder.g<>(resourcekey, lifecycle, registrysetbuilder_e));
        return this;
    }

    public <T> RegistrySetBuilder add(ResourceKey<? extends IRegistry<T>> resourcekey, RegistrySetBuilder.e<T> registrysetbuilder_e) {
        return this.add(resourcekey, Lifecycle.stable(), registrysetbuilder_e);
    }

    private RegistrySetBuilder.a createState(IRegistryCustom iregistrycustom) {
        RegistrySetBuilder.a registrysetbuilder_a = RegistrySetBuilder.a.create(iregistrycustom, this.entries.stream().map(RegistrySetBuilder.g::key));

        this.entries.forEach((registrysetbuilder_g) -> {
            registrysetbuilder_g.apply(registrysetbuilder_a);
        });
        return registrysetbuilder_a;
    }

    public HolderLookup.b build(IRegistryCustom iregistrycustom) {
        RegistrySetBuilder.a registrysetbuilder_a = this.createState(iregistrycustom);
        Stream<HolderLookup.c<?>> stream = iregistrycustom.registries().map((iregistrycustom_d) -> {
            return iregistrycustom_d.value().asLookup();
        });
        Stream<HolderLookup.c<?>> stream1 = this.entries.stream().map((registrysetbuilder_g) -> {
            return registrysetbuilder_g.collectChanges(registrysetbuilder_a).buildAsLookup();
        });

        Objects.requireNonNull(registrysetbuilder_a);
        HolderLookup.b holderlookup_b = HolderLookup.b.create(Stream.concat(stream, stream1.peek(registrysetbuilder_a::addOwner)));

        registrysetbuilder_a.reportRemainingUnreferencedValues();
        registrysetbuilder_a.throwOnError();
        return holderlookup_b;
    }

    public HolderLookup.b buildPatch(IRegistryCustom iregistrycustom, HolderLookup.b holderlookup_b) {
        RegistrySetBuilder.a registrysetbuilder_a = this.createState(iregistrycustom);
        Map<ResourceKey<? extends IRegistry<?>>, RegistrySetBuilder.f<?>> map = new HashMap();

        registrysetbuilder_a.collectReferencedRegistries().forEach((registrysetbuilder_f) -> {
            map.put(registrysetbuilder_f.key, registrysetbuilder_f);
        });
        this.entries.stream().map((registrysetbuilder_g) -> {
            return registrysetbuilder_g.collectChanges(registrysetbuilder_a);
        }).forEach((registrysetbuilder_f) -> {
            map.put(registrysetbuilder_f.key, registrysetbuilder_f);
        });
        Stream<HolderLookup.c<?>> stream = iregistrycustom.registries().map((iregistrycustom_d) -> {
            return iregistrycustom_d.value().asLookup();
        });
        Stream stream1 = map.values().stream().map(RegistrySetBuilder.f::buildAsLookup);

        Objects.requireNonNull(registrysetbuilder_a);
        HolderLookup.b holderlookup_b1 = HolderLookup.b.create(Stream.concat(stream, stream1.peek(registrysetbuilder_a::addOwner)));

        registrysetbuilder_a.fillMissingHolders(holderlookup_b);
        registrysetbuilder_a.reportRemainingUnreferencedValues();
        registrysetbuilder_a.throwOnError();
        return holderlookup_b1;
    }

    private static record g<T> (ResourceKey<? extends IRegistry<T>> key, Lifecycle lifecycle, RegistrySetBuilder.e<T> bootstrap) {

        void apply(RegistrySetBuilder.a registrysetbuilder_a) {
            this.bootstrap.run(registrysetbuilder_a.bootstapContext());
        }

        public RegistrySetBuilder.f<T> collectChanges(RegistrySetBuilder.a registrysetbuilder_a) {
            Map<ResourceKey<T>, RegistrySetBuilder.i<T>> map = new HashMap();
            Iterator iterator = registrysetbuilder_a.registeredValues.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ResourceKey<?>, RegistrySetBuilder.d<?>> entry = (Entry) iterator.next();
                ResourceKey<?> resourcekey = (ResourceKey) entry.getKey();

                if (resourcekey.isFor(this.key)) {
                    RegistrySetBuilder.d<T> registrysetbuilder_d = (RegistrySetBuilder.d) entry.getValue();
                    Holder.c<T> holder_c = (Holder.c) registrysetbuilder_a.lookup.holders.remove(resourcekey);

                    map.put(resourcekey, new RegistrySetBuilder.i<>(registrysetbuilder_d, Optional.ofNullable(holder_c)));
                    iterator.remove();
                }
            }

            return new RegistrySetBuilder.f<>(this.key, this.lifecycle, map);
        }
    }

    @FunctionalInterface
    public interface e<T> {

        void run(BootstapContext<T> bootstapcontext);
    }

    private static record a(RegistrySetBuilder.b owner, RegistrySetBuilder.h lookup, Map<MinecraftKey, HolderGetter<?>> registries, Map<ResourceKey<?>, RegistrySetBuilder.d<?>> registeredValues, List<RuntimeException> errors) {

        public static RegistrySetBuilder.a create(IRegistryCustom iregistrycustom, Stream<ResourceKey<? extends IRegistry<?>>> stream) {
            RegistrySetBuilder.b registrysetbuilder_b = new RegistrySetBuilder.b();
            List<RuntimeException> list = new ArrayList();
            RegistrySetBuilder.h registrysetbuilder_h = new RegistrySetBuilder.h(registrysetbuilder_b);
            Builder<MinecraftKey, HolderGetter<?>> builder = ImmutableMap.builder();

            iregistrycustom.registries().forEach((iregistrycustom_d) -> {
                builder.put(iregistrycustom_d.key().location(), RegistrySetBuilder.wrapContextLookup(iregistrycustom_d.value().asLookup()));
            });
            stream.forEach((resourcekey) -> {
                builder.put(resourcekey.location(), registrysetbuilder_h);
            });
            return new RegistrySetBuilder.a(registrysetbuilder_b, registrysetbuilder_h, builder.build(), new HashMap(), list);
        }

        public <T> BootstapContext<T> bootstapContext() {
            return new BootstapContext<T>() {
                @Override
                public Holder.c<T> register(ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle) {
                    RegistrySetBuilder.d<?> registrysetbuilder_d = (RegistrySetBuilder.d) a.this.registeredValues.put(resourcekey, new RegistrySetBuilder.d<>(t0, lifecycle));

                    if (registrysetbuilder_d != null) {
                        a.this.errors.add(new IllegalStateException("Duplicate registration for " + resourcekey + ", new=" + t0 + ", old=" + registrysetbuilder_d.value));
                    }

                    return a.this.lookup.getOrCreate(resourcekey);
                }

                @Override
                public <S> HolderGetter<S> lookup(ResourceKey<? extends IRegistry<? extends S>> resourcekey) {
                    return (HolderGetter) a.this.registries.getOrDefault(resourcekey.location(), a.this.lookup);
                }
            };
        }

        public void reportRemainingUnreferencedValues() {
            Iterator iterator = this.lookup.holders.keySet().iterator();

            while (iterator.hasNext()) {
                ResourceKey<Object> resourcekey = (ResourceKey) iterator.next();

                this.errors.add(new IllegalStateException("Unreferenced key: " + resourcekey));
            }

            this.registeredValues.forEach((resourcekey1, registrysetbuilder_d) -> {
                this.errors.add(new IllegalStateException("Orpaned value " + registrysetbuilder_d.value + " for key " + resourcekey1));
            });
        }

        public void throwOnError() {
            if (!this.errors.isEmpty()) {
                IllegalStateException illegalstateexception = new IllegalStateException("Errors during registry creation");
                Iterator iterator = this.errors.iterator();

                while (iterator.hasNext()) {
                    RuntimeException runtimeexception = (RuntimeException) iterator.next();

                    illegalstateexception.addSuppressed(runtimeexception);
                }

                throw illegalstateexception;
            }
        }

        public void addOwner(HolderOwner<?> holderowner) {
            this.owner.add(holderowner);
        }

        public void fillMissingHolders(HolderLookup.b holderlookup_b) {
            Map<MinecraftKey, Optional<? extends HolderLookup<Object>>> map = new HashMap();
            Iterator iterator = this.lookup.holders.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ResourceKey<Object>, Holder.c<Object>> entry = (Entry) iterator.next();
                ResourceKey<Object> resourcekey = (ResourceKey) entry.getKey();
                Holder.c<Object> holder_c = (Holder.c) entry.getValue();

                ((Optional) map.computeIfAbsent(resourcekey.registry(), (minecraftkey) -> {
                    return holderlookup_b.lookup(ResourceKey.createRegistryKey(minecraftkey));
                })).flatMap((holderlookup) -> {
                    return holderlookup.get(resourcekey);
                }).ifPresent((holder_c1) -> {
                    holder_c.bindValue(holder_c1.value());
                    iterator.remove();
                });
            }

        }

        public Stream<RegistrySetBuilder.f<?>> collectReferencedRegistries() {
            return this.lookup.holders.keySet().stream().map(ResourceKey::registry).distinct().map((minecraftkey) -> {
                return new RegistrySetBuilder.f<>(ResourceKey.createRegistryKey(minecraftkey), Lifecycle.stable(), Map.of());
            });
        }
    }

    private static record f<T> (ResourceKey<? extends IRegistry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, RegistrySetBuilder.i<T>> values) {

        public HolderLookup.c<T> buildAsLookup() {
            return new HolderLookup.c<T>() {
                private final Map<ResourceKey<T>, Holder.c<T>> entries;

                {
                    this.entries = (Map) f.this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Entry::getKey, (entry) -> {
                        RegistrySetBuilder.i<T> registrysetbuilder_i = (RegistrySetBuilder.i) entry.getValue();
                        Holder.c<T> holder_c = (Holder.c) registrysetbuilder_i.holder().orElseGet(() -> {
                            return Holder.c.createStandAlone(this, (ResourceKey) entry.getKey());
                        });

                        holder_c.bindValue(registrysetbuilder_i.value().value());
                        return holder_c;
                    }));
                }

                @Override
                public ResourceKey<? extends IRegistry<? extends T>> key() {
                    return f.this.key;
                }

                @Override
                public Lifecycle registryLifecycle() {
                    return f.this.lifecycle;
                }

                @Override
                public Optional<Holder.c<T>> get(ResourceKey<T> resourcekey) {
                    return Optional.ofNullable((Holder.c) this.entries.get(resourcekey));
                }

                @Override
                public Stream<Holder.c<T>> listElements() {
                    return this.entries.values().stream();
                }

                @Override
                public Optional<HolderSet.Named<T>> get(TagKey<T> tagkey) {
                    return Optional.empty();
                }

                @Override
                public Stream<HolderSet.Named<T>> listTags() {
                    return Stream.empty();
                }
            };
        }
    }

    private static record i<T> (RegistrySetBuilder.d<T> value, Optional<Holder.c<T>> holder) {

    }

    private static record d<T> (T value, Lifecycle lifecycle) {

    }

    private static class h extends RegistrySetBuilder.c<Object> {

        final Map<ResourceKey<Object>, Holder.c<Object>> holders = new HashMap();

        public h(HolderOwner<Object> holderowner) {
            super(holderowner);
        }

        @Override
        public Optional<Holder.c<Object>> get(ResourceKey<Object> resourcekey) {
            return Optional.of(this.getOrCreate(resourcekey));
        }

        <T> Holder.c<T> getOrCreate(ResourceKey<T> resourcekey) {
            return (Holder.c) this.holders.computeIfAbsent(resourcekey, (resourcekey1) -> {
                return Holder.c.createStandAlone(this.owner, resourcekey1);
            });
        }
    }

    private static class b implements HolderOwner<Object> {

        private final Set<HolderOwner<?>> owners = Sets.newIdentityHashSet();

        b() {}

        @Override
        public boolean canSerializeIn(HolderOwner<Object> holderowner) {
            return this.owners.contains(holderowner);
        }

        public void add(HolderOwner<?> holderowner) {
            this.owners.add(holderowner);
        }
    }

    private abstract static class c<T> implements HolderGetter<T> {

        protected final HolderOwner<T> owner;

        protected c(HolderOwner<T> holderowner) {
            this.owner = holderowner;
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> tagkey) {
            return Optional.of(HolderSet.emptyNamed(this.owner, tagkey));
        }
    }
}

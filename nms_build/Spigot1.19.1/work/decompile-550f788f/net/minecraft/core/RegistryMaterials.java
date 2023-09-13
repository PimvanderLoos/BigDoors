package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class RegistryMaterials<T> extends IRegistryWritable<T> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final ObjectList<Holder.c<T>> byId = new ObjectArrayList(256);
    private final Object2IntMap<T> toId = (Object2IntMap) SystemUtils.make(new Object2IntOpenCustomHashMap(SystemUtils.identityStrategy()), (object2intopencustomhashmap) -> {
        object2intopencustomhashmap.defaultReturnValue(-1);
    });
    private final Map<MinecraftKey, Holder.c<T>> byLocation = new HashMap();
    private final Map<ResourceKey<T>, Holder.c<T>> byKey = new HashMap();
    private final Map<T, Holder.c<T>> byValue = new IdentityHashMap();
    private final Map<T, Lifecycle> lifecycles = new IdentityHashMap();
    private Lifecycle elementsLifecycle;
    private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap();
    private boolean frozen;
    @Nullable
    private final Function<T, Holder.c<T>> customHolderProvider;
    @Nullable
    private Map<T, Holder.c<T>> intrusiveHolderCache;
    @Nullable
    private List<Holder.c<T>> holdersInOrder;
    private int nextId;

    public RegistryMaterials(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, @Nullable Function<T, Holder.c<T>> function) {
        super(resourcekey, lifecycle);
        this.elementsLifecycle = lifecycle;
        this.customHolderProvider = function;
        if (function != null) {
            this.intrusiveHolderCache = new IdentityHashMap();
        }

    }

    private List<Holder.c<T>> holdersInOrder() {
        if (this.holdersInOrder == null) {
            this.holdersInOrder = this.byId.stream().filter(Objects::nonNull).toList();
        }

        return this.holdersInOrder;
    }

    private void validateWrite(ResourceKey<T> resourcekey) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is already frozen (trying to add key " + resourcekey + ")");
        }
    }

    @Override
    public Holder<T> registerMapping(int i, ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle) {
        return this.registerMapping(i, resourcekey, t0, lifecycle, true);
    }

    private Holder<T> registerMapping(int i, ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle, boolean flag) {
        this.validateWrite(resourcekey);
        Validate.notNull(resourcekey);
        Validate.notNull(t0);
        this.byId.size(Math.max(this.byId.size(), i + 1));
        this.toId.put(t0, i);
        this.holdersInOrder = null;
        if (flag && this.byKey.containsKey(resourcekey)) {
            SystemUtils.logAndPauseIfInIde("Adding duplicate key '" + resourcekey + "' to registry");
        }

        if (this.byValue.containsKey(t0)) {
            SystemUtils.logAndPauseIfInIde("Adding duplicate value '" + t0 + "' to registry");
        }

        this.lifecycles.put(t0, lifecycle);
        this.elementsLifecycle = this.elementsLifecycle.add(lifecycle);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }

        Holder.c holder_c;

        if (this.customHolderProvider != null) {
            holder_c = (Holder.c) this.customHolderProvider.apply(t0);
            Holder.c<T> holder_c1 = (Holder.c) this.byKey.put(resourcekey, holder_c);

            if (holder_c1 != null && holder_c1 != holder_c) {
                throw new IllegalStateException("Invalid holder present for key " + resourcekey);
            }
        } else {
            holder_c = (Holder.c) this.byKey.computeIfAbsent(resourcekey, (resourcekey1) -> {
                return Holder.c.createStandAlone(this, resourcekey1);
            });
        }

        this.byLocation.put(resourcekey.location(), holder_c);
        this.byValue.put(t0, holder_c);
        holder_c.bind(resourcekey, t0);
        this.byId.set(i, holder_c);
        return holder_c;
    }

    @Override
    public Holder<T> register(ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle) {
        return this.registerMapping(this.nextId, resourcekey, t0, lifecycle);
    }

    @Override
    public Holder<T> registerOrOverride(OptionalInt optionalint, ResourceKey<T> resourcekey, T t0, Lifecycle lifecycle) {
        this.validateWrite(resourcekey);
        Validate.notNull(resourcekey);
        Validate.notNull(t0);
        Holder<T> holder = (Holder) this.byKey.get(resourcekey);
        T t1 = holder != null && holder.isBound() ? holder.value() : null;
        int i;

        if (t1 == null) {
            i = optionalint.orElse(this.nextId);
        } else {
            i = this.toId.getInt(t1);
            if (optionalint.isPresent() && optionalint.getAsInt() != i) {
                throw new IllegalStateException("ID mismatch");
            }

            this.lifecycles.remove(t1);
            this.toId.removeInt(t1);
            this.byValue.remove(t1);
        }

        return this.registerMapping(i, resourcekey, t0, lifecycle, false);
    }

    @Nullable
    @Override
    public MinecraftKey getKey(T t0) {
        Holder.c<T> holder_c = (Holder.c) this.byValue.get(t0);

        return holder_c != null ? holder_c.key().location() : null;
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T t0) {
        return Optional.ofNullable((Holder.c) this.byValue.get(t0)).map(Holder.c::key);
    }

    @Override
    public int getId(@Nullable T t0) {
        return this.toId.getInt(t0);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceKey<T> resourcekey) {
        return getValueFromNullable((Holder.c) this.byKey.get(resourcekey));
    }

    @Nullable
    @Override
    public T byId(int i) {
        return i >= 0 && i < this.byId.size() ? getValueFromNullable((Holder.c) this.byId.get(i)) : null;
    }

    @Override
    public Optional<Holder<T>> getHolder(int i) {
        return i >= 0 && i < this.byId.size() ? Optional.ofNullable((Holder) this.byId.get(i)) : Optional.empty();
    }

    @Override
    public Optional<Holder<T>> getHolder(ResourceKey<T> resourcekey) {
        return Optional.ofNullable((Holder) this.byKey.get(resourcekey));
    }

    @Override
    public Holder<T> getOrCreateHolderOrThrow(ResourceKey<T> resourcekey) {
        return (Holder) this.byKey.computeIfAbsent(resourcekey, (resourcekey1) -> {
            if (this.customHolderProvider != null) {
                throw new IllegalStateException("This registry can't create new holders without value");
            } else {
                this.validateWrite(resourcekey1);
                return Holder.c.createStandAlone(this, resourcekey1);
            }
        });
    }

    @Override
    public DataResult<Holder<T>> getOrCreateHolder(ResourceKey<T> resourcekey) {
        Holder.c<T> holder_c = (Holder.c) this.byKey.get(resourcekey);

        if (holder_c == null) {
            if (this.customHolderProvider != null) {
                return DataResult.error("This registry can't create new holders without value (requested key: " + resourcekey + ")");
            }

            if (this.frozen) {
                return DataResult.error("Registry is already frozen (requested key: " + resourcekey + ")");
            }

            holder_c = Holder.c.createStandAlone(this, resourcekey);
            this.byKey.put(resourcekey, holder_c);
        }

        return DataResult.success(holder_c);
    }

    @Override
    public int size() {
        return this.byKey.size();
    }

    @Override
    public Lifecycle lifecycle(T t0) {
        return (Lifecycle) this.lifecycles.get(t0);
    }

    @Override
    public Lifecycle elementsLifecycle() {
        return this.elementsLifecycle;
    }

    public Iterator<T> iterator() {
        return Iterators.transform(this.holdersInOrder().iterator(), Holder::value);
    }

    @Nullable
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        Holder.c<T> holder_c = (Holder.c) this.byLocation.get(minecraftkey);

        return getValueFromNullable(holder_c);
    }

    @Nullable
    private static <T> T getValueFromNullable(@Nullable Holder.c<T> holder_c) {
        return holder_c != null ? holder_c.value() : null;
    }

    @Override
    public Set<MinecraftKey> keySet() {
        return Collections.unmodifiableSet(this.byLocation.keySet());
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return Collections.unmodifiableSet(this.byKey.keySet());
    }

    @Override
    public Set<Entry<ResourceKey<T>, T>> entrySet() {
        return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Holder::value).entrySet());
    }

    @Override
    public Stream<Holder.c<T>> holders() {
        return this.holdersInOrder().stream();
    }

    @Override
    public boolean isKnownTagName(TagKey<T> tagkey) {
        return this.tags.containsKey(tagkey);
    }

    @Override
    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return this.tags.entrySet().stream().map((entry) -> {
            return Pair.of((TagKey) entry.getKey(), (HolderSet.Named) entry.getValue());
        });
    }

    @Override
    public HolderSet.Named<T> getOrCreateTag(TagKey<T> tagkey) {
        HolderSet.Named<T> holderset_named = (HolderSet.Named) this.tags.get(tagkey);

        if (holderset_named == null) {
            holderset_named = this.createTag(tagkey);
            Map<TagKey<T>, HolderSet.Named<T>> map = new IdentityHashMap(this.tags);

            map.put(tagkey, holderset_named);
            this.tags = map;
        }

        return holderset_named;
    }

    private HolderSet.Named<T> createTag(TagKey<T> tagkey) {
        return new HolderSet.Named<>(this, tagkey);
    }

    @Override
    public Stream<TagKey<T>> getTagNames() {
        return this.tags.keySet().stream();
    }

    @Override
    public boolean isEmpty() {
        return this.byKey.isEmpty();
    }

    @Override
    public Optional<Holder<T>> getRandom(RandomSource randomsource) {
        return SystemUtils.getRandomSafe(this.holdersInOrder(), randomsource).map(Holder::hackyErase);
    }

    @Override
    public boolean containsKey(MinecraftKey minecraftkey) {
        return this.byLocation.containsKey(minecraftkey);
    }

    @Override
    public boolean containsKey(ResourceKey<T> resourcekey) {
        return this.byKey.containsKey(resourcekey);
    }

    @Override
    public IRegistry<T> freeze() {
        this.frozen = true;
        List<MinecraftKey> list = this.byKey.entrySet().stream().filter((entry) -> {
            return !((Holder.c) entry.getValue()).isBound();
        }).map((entry) -> {
            return ((ResourceKey) entry.getKey()).location();
        }).sorted().toList();

        if (!list.isEmpty()) {
            ResourceKey resourcekey = this.key();

            throw new IllegalStateException("Unbound values in registry " + resourcekey + ": " + list);
        } else {
            if (this.intrusiveHolderCache != null) {
                List<Holder.c<T>> list1 = this.intrusiveHolderCache.values().stream().filter((holder_c) -> {
                    return !holder_c.isBound();
                }).toList();

                if (!list1.isEmpty()) {
                    throw new IllegalStateException("Some intrusive holders were not added to registry: " + list1);
                }

                this.intrusiveHolderCache = null;
            }

            return this;
        }
    }

    @Override
    public Holder.c<T> createIntrusiveHolder(T t0) {
        if (this.customHolderProvider == null) {
            throw new IllegalStateException("This registry can't create intrusive holders");
        } else if (!this.frozen && this.intrusiveHolderCache != null) {
            return (Holder.c) this.intrusiveHolderCache.computeIfAbsent(t0, (object) -> {
                return Holder.c.createIntrusive(this, object);
            });
        } else {
            throw new IllegalStateException("Registry is already frozen");
        }
    }

    @Override
    public Optional<HolderSet.Named<T>> getTag(TagKey<T> tagkey) {
        return Optional.ofNullable((HolderSet.Named) this.tags.get(tagkey));
    }

    @Override
    public void bindTags(Map<TagKey<T>, List<Holder<T>>> map) {
        Map<Holder.c<T>, List<TagKey<T>>> map1 = new IdentityHashMap();

        this.byKey.values().forEach((holder_c) -> {
            map1.put(holder_c, new ArrayList());
        });
        map.forEach((tagkey, list) -> {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Holder<T> holder = (Holder) iterator.next();

                if (!holder.isValidInRegistry(this)) {
                    throw new IllegalStateException("Can't create named set " + tagkey + " containing value " + holder + " from outside registry " + this);
                }

                if (!(holder instanceof Holder.c)) {
                    throw new IllegalStateException("Found direct holder " + holder + " value in tag " + tagkey);
                }

                Holder.c<T> holder_c = (Holder.c) holder;

                ((List) map1.get(holder_c)).add(tagkey);
            }

        });
        Set<TagKey<T>> set = Sets.difference(this.tags.keySet(), map.keySet());

        if (!set.isEmpty()) {
            RegistryMaterials.LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.key(), set.stream().map((tagkey) -> {
                return tagkey.location().toString();
            }).sorted().collect(Collectors.joining(", ")));
        }

        Map<TagKey<T>, HolderSet.Named<T>> map2 = new IdentityHashMap(this.tags);

        map.forEach((tagkey, list) -> {
            ((HolderSet.Named) map2.computeIfAbsent(tagkey, this::createTag)).bind(list);
        });
        map1.forEach(Holder.c::bindTags);
        this.tags = map2;
    }

    @Override
    public void resetTags() {
        this.tags.values().forEach((holderset_named) -> {
            holderset_named.bind(List.of());
        });
        this.byKey.values().forEach((holder_c) -> {
            holder_c.bindTags(Set.of());
        });
    }
}

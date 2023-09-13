package net.minecraft.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryDataPackCodec;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryMaterials<T> extends IRegistryWritable<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private final ObjectList<T> byId = new ObjectArrayList(256);
    private final Object2IntMap<T> toId = (Object2IntMap) SystemUtils.make(new Object2IntOpenCustomHashMap(SystemUtils.identityStrategy()), (object2intopencustomhashmap) -> {
        object2intopencustomhashmap.defaultReturnValue(-1);
    });
    private final BiMap<MinecraftKey, T> storage = HashBiMap.create();
    private final BiMap<ResourceKey<T>, T> keyStorage = HashBiMap.create();
    private final Map<T, Lifecycle> lifecycles = Maps.newIdentityHashMap();
    private Lifecycle elementsLifecycle;
    @Nullable
    protected Object[] randomCache;
    private int nextId;

    public RegistryMaterials(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
        this.elementsLifecycle = lifecycle;
    }

    public static <T> MapCodec<RegistryMaterials.a<T>> withNameAndId(ResourceKey<? extends IRegistry<T>> resourcekey, MapCodec<T> mapcodec) {
        return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.CODEC.xmap(ResourceKey.elementKey(resourcekey), ResourceKey::location).fieldOf("name").forGetter(RegistryMaterials.a::key), Codec.INT.fieldOf("id").forGetter(RegistryMaterials.a::id), mapcodec.forGetter(RegistryMaterials.a::value)).apply(instance, RegistryMaterials.a::new);
        });
    }

    @Override
    public <V extends T> V registerMapping(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
        return this.registerMapping(i, resourcekey, v0, lifecycle, true);
    }

    private <V extends T> V registerMapping(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle, boolean flag) {
        Validate.notNull(resourcekey);
        Validate.notNull(v0);
        this.byId.size(Math.max(this.byId.size(), i + 1));
        this.byId.set(i, v0);
        this.toId.put(v0, i);
        this.randomCache = null;
        if (flag && this.keyStorage.containsKey(resourcekey)) {
            SystemUtils.logAndPauseIfInIde("Adding duplicate key '" + resourcekey + "' to registry");
        }

        if (this.storage.containsValue(v0)) {
            SystemUtils.logAndPauseIfInIde("Adding duplicate value '" + v0 + "' to registry");
        }

        this.storage.put(resourcekey.location(), v0);
        this.keyStorage.put(resourcekey, v0);
        this.lifecycles.put(v0, lifecycle);
        this.elementsLifecycle = this.elementsLifecycle.add(lifecycle);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }

        return v0;
    }

    @Override
    public <V extends T> V register(ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
        return this.registerMapping(this.nextId, resourcekey, v0, lifecycle);
    }

    @Override
    public <V extends T> V registerOrOverride(OptionalInt optionalint, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
        Validate.notNull(resourcekey);
        Validate.notNull(v0);
        T t0 = this.keyStorage.get(resourcekey);
        int i;

        if (t0 == null) {
            i = optionalint.isPresent() ? optionalint.getAsInt() : this.nextId;
        } else {
            i = this.toId.getInt(t0);
            if (optionalint.isPresent() && optionalint.getAsInt() != i) {
                throw new IllegalStateException("ID mismatch");
            }

            this.toId.removeInt(t0);
            this.lifecycles.remove(t0);
        }

        return this.registerMapping(i, resourcekey, v0, lifecycle, false);
    }

    @Nullable
    @Override
    public MinecraftKey getKey(T t0) {
        return (MinecraftKey) this.storage.inverse().get(t0);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T t0) {
        return Optional.ofNullable((ResourceKey) this.keyStorage.inverse().get(t0));
    }

    @Override
    public int getId(@Nullable T t0) {
        return this.toId.getInt(t0);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceKey<T> resourcekey) {
        return this.keyStorage.get(resourcekey);
    }

    @Nullable
    @Override
    public T byId(int i) {
        return i >= 0 && i < this.byId.size() ? this.byId.get(i) : null;
    }

    @Override
    public int size() {
        return this.storage.size();
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
        return Iterators.filter(this.byId.iterator(), Objects::nonNull);
    }

    @Nullable
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        return this.storage.get(minecraftkey);
    }

    @Override
    public Set<MinecraftKey> keySet() {
        return Collections.unmodifiableSet(this.storage.keySet());
    }

    @Override
    public Set<Entry<ResourceKey<T>, T>> entrySet() {
        return Collections.unmodifiableMap(this.keyStorage).entrySet();
    }

    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    @Nullable
    @Override
    public T getRandom(Random random) {
        if (this.randomCache == null) {
            Collection<?> collection = this.storage.values();

            if (collection.isEmpty()) {
                return null;
            }

            this.randomCache = collection.toArray((i) -> {
                return new Object[i];
            });
        }

        return SystemUtils.getRandom(this.randomCache, random);
    }

    @Override
    public boolean containsKey(MinecraftKey minecraftkey) {
        return this.storage.containsKey(minecraftkey);
    }

    @Override
    public boolean containsKey(ResourceKey<T> resourcekey) {
        return this.keyStorage.containsKey(resourcekey);
    }

    public static <T> Codec<RegistryMaterials<T>> networkCodec(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return withNameAndId(resourcekey, codec.fieldOf("element")).codec().listOf().xmap((list) -> {
            RegistryMaterials<T> registrymaterials = new RegistryMaterials<>(resourcekey, lifecycle);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                RegistryMaterials.a<T> registrymaterials_a = (RegistryMaterials.a) iterator.next();

                registrymaterials.registerMapping(registrymaterials_a.id(), registrymaterials_a.key(), registrymaterials_a.value(), lifecycle);
            }

            return registrymaterials;
        }, (registrymaterials) -> {
            Builder<RegistryMaterials.a<T>> builder = ImmutableList.builder();
            Iterator iterator = registrymaterials.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();

                builder.add(new RegistryMaterials.a<>((ResourceKey) registrymaterials.getResourceKey(t0).get(), registrymaterials.getId(t0), t0));
            }

            return builder.build();
        });
    }

    public static <T> Codec<RegistryMaterials<T>> dataPackCodec(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return RegistryDataPackCodec.create(resourcekey, lifecycle, codec);
    }

    public static <T> Codec<RegistryMaterials<T>> directCodec(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.unboundedMap(MinecraftKey.CODEC.xmap(ResourceKey.elementKey(resourcekey), ResourceKey::location), codec).xmap((map) -> {
            RegistryMaterials<T> registrymaterials = new RegistryMaterials<>(resourcekey, lifecycle);

            map.forEach((resourcekey1, object) -> {
                registrymaterials.register(resourcekey1, object, lifecycle);
            });
            return registrymaterials;
        }, (registrymaterials) -> {
            return ImmutableMap.copyOf(registrymaterials.keyStorage);
        });
    }

    private static record a<T> (ResourceKey<T> a, int b, T c) {

        private final ResourceKey<T> key;
        private final int id;
        private final T value;

        a(ResourceKey<T> resourcekey, int i, T t0) {
            this.key = resourcekey;
            this.id = i;
            this.value = t0;
        }

        public ResourceKey<T> key() {
            return this.key;
        }

        public int id() {
            return this.id;
        }

        public T value() {
            return this.value;
        }
    }
}

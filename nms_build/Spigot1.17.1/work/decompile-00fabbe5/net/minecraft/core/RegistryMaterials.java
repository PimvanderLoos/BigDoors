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
    private final Object2IntMap<T> toId = new Object2IntOpenCustomHashMap(SystemUtils.k());
    private final BiMap<MinecraftKey, T> storage;
    private final BiMap<ResourceKey<T>, T> keyStorage;
    private final Map<T, Lifecycle> lifecycles;
    private Lifecycle elementsLifecycle;
    protected Object[] randomCache;
    private int nextId;

    public RegistryMaterials(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
        this.toId.defaultReturnValue(-1);
        this.storage = HashBiMap.create();
        this.keyStorage = HashBiMap.create();
        this.lifecycles = Maps.newIdentityHashMap();
        this.elementsLifecycle = lifecycle;
    }

    public static <T> MapCodec<RegistryMaterials.a<T>> a(ResourceKey<? extends IRegistry<T>> resourcekey, MapCodec<T> mapcodec) {
        return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(MinecraftKey.CODEC.xmap(ResourceKey.b(resourcekey), ResourceKey::a).fieldOf("name").forGetter((registrymaterials_a) -> {
                return registrymaterials_a.key;
            }), Codec.INT.fieldOf("id").forGetter((registrymaterials_a) -> {
                return registrymaterials_a.id;
            }), mapcodec.forGetter((registrymaterials_a) -> {
                return registrymaterials_a.value;
            })).apply(instance, RegistryMaterials.a::new);
        });
    }

    @Override
    public <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
        return this.a(i, resourcekey, v0, lifecycle, true);
    }

    private <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle, boolean flag) {
        Validate.notNull(resourcekey);
        Validate.notNull(v0);
        this.byId.size(Math.max(this.byId.size(), i + 1));
        this.byId.set(i, v0);
        this.toId.put(v0, i);
        this.randomCache = null;
        if (flag && this.keyStorage.containsKey(resourcekey)) {
            RegistryMaterials.LOGGER.debug("Adding duplicate key '{}' to registry", resourcekey);
        }

        if (this.storage.containsValue(v0)) {
            RegistryMaterials.LOGGER.error("Adding duplicate value '{}' to registry", v0);
        }

        this.storage.put(resourcekey.a(), v0);
        this.keyStorage.put(resourcekey, v0);
        this.lifecycles.put(v0, lifecycle);
        this.elementsLifecycle = this.elementsLifecycle.add(lifecycle);
        if (this.nextId <= i) {
            this.nextId = i + 1;
        }

        return v0;
    }

    @Override
    public <V extends T> V a(ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
        return this.a(this.nextId, resourcekey, v0, lifecycle);
    }

    @Override
    public <V extends T> V a(OptionalInt optionalint, ResourceKey<T> resourcekey, V v0, Lifecycle lifecycle) {
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

        return this.a(i, resourcekey, v0, lifecycle, false);
    }

    @Nullable
    @Override
    public MinecraftKey getKey(T t0) {
        return (MinecraftKey) this.storage.inverse().get(t0);
    }

    @Override
    public Optional<ResourceKey<T>> c(T t0) {
        return Optional.ofNullable((ResourceKey) this.keyStorage.inverse().get(t0));
    }

    @Override
    public int getId(@Nullable T t0) {
        return this.toId.getInt(t0);
    }

    @Nullable
    @Override
    public T a(@Nullable ResourceKey<T> resourcekey) {
        return this.keyStorage.get(resourcekey);
    }

    @Nullable
    @Override
    public T fromId(int i) {
        return i >= 0 && i < this.byId.size() ? this.byId.get(i) : null;
    }

    @Override
    public Lifecycle d(T t0) {
        return (Lifecycle) this.lifecycles.get(t0);
    }

    @Override
    public Lifecycle b() {
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
    public Set<Entry<ResourceKey<T>, T>> d() {
        return Collections.unmodifiableMap(this.keyStorage).entrySet();
    }

    @Override
    public boolean e() {
        return this.storage.isEmpty();
    }

    @Nullable
    @Override
    public T a(Random random) {
        if (this.randomCache == null) {
            Collection<?> collection = this.storage.values();

            if (collection.isEmpty()) {
                return null;
            }

            this.randomCache = collection.toArray(new Object[collection.size()]);
        }

        return SystemUtils.a(this.randomCache, random);
    }

    @Override
    public boolean c(MinecraftKey minecraftkey) {
        return this.storage.containsKey(minecraftkey);
    }

    @Override
    public boolean b(ResourceKey<T> resourcekey) {
        return this.keyStorage.containsKey(resourcekey);
    }

    public static <T> Codec<RegistryMaterials<T>> a(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return a(resourcekey, codec.fieldOf("element")).codec().listOf().xmap((list) -> {
            RegistryMaterials<T> registrymaterials = new RegistryMaterials<>(resourcekey, lifecycle);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                RegistryMaterials.a<T> registrymaterials_a = (RegistryMaterials.a) iterator.next();

                registrymaterials.a(registrymaterials_a.id, registrymaterials_a.key, registrymaterials_a.value, lifecycle);
            }

            return registrymaterials;
        }, (registrymaterials) -> {
            Builder<RegistryMaterials.a<T>> builder = ImmutableList.builder();
            Iterator iterator = registrymaterials.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();

                builder.add(new RegistryMaterials.a<>((ResourceKey) registrymaterials.c(t0).get(), registrymaterials.getId(t0), t0));
            }

            return builder.build();
        });
    }

    public static <T> Codec<RegistryMaterials<T>> b(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return RegistryDataPackCodec.a(resourcekey, lifecycle, codec);
    }

    public static <T> Codec<RegistryMaterials<T>> c(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.unboundedMap(MinecraftKey.CODEC.xmap(ResourceKey.b(resourcekey), ResourceKey::a), codec).xmap((map) -> {
            RegistryMaterials<T> registrymaterials = new RegistryMaterials<>(resourcekey, lifecycle);

            map.forEach((resourcekey1, object) -> {
                registrymaterials.a(resourcekey1, object, lifecycle);
            });
            return registrymaterials;
        }, (registrymaterials) -> {
            return ImmutableMap.copyOf(registrymaterials.keyStorage);
        });
    }

    public static class a<T> {

        public final ResourceKey<T> key;
        public final int id;
        public final T value;

        public a(ResourceKey<T> resourcekey, int i, T t0) {
            this.key = resourcekey;
            this.id = i;
            this.value = t0;
        }
    }
}

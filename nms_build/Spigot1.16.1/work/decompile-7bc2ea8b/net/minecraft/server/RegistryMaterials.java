package net.minecraft.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryMaterials<T> extends IRegistryWritable<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    protected final RegistryID<T> b = new RegistryID<>(256);
    protected final BiMap<MinecraftKey, T> c = HashBiMap.create();
    private final BiMap<ResourceKey<T>, T> bb = HashBiMap.create();
    private final Set<ResourceKey<T>> bc = Sets.newIdentityHashSet();
    protected Object[] d;
    private int bd;

    public RegistryMaterials(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        super(resourcekey, lifecycle);
    }

    @Override
    public <V extends T> V a(int i, ResourceKey<T> resourcekey, V v0) {
        this.b.a(v0, i);
        Validate.notNull(resourcekey);
        Validate.notNull(v0);
        this.d = null;
        if (this.bb.containsKey(resourcekey)) {
            RegistryMaterials.LOGGER.debug("Adding duplicate key '{}' to registry", resourcekey);
        }

        this.c.put(resourcekey.a(), v0);
        this.bb.put(resourcekey, v0);
        if (this.bd <= i) {
            this.bd = i + 1;
        }

        return v0;
    }

    @Override
    public <V extends T> V a(ResourceKey<T> resourcekey, V v0) {
        return this.a(this.bd, resourcekey, v0);
    }

    @Nullable
    @Override
    public MinecraftKey getKey(T t0) {
        return (MinecraftKey) this.c.inverse().get(t0);
    }

    @Override
    public Optional<ResourceKey<T>> c(T t0) {
        return Optional.ofNullable(this.bb.inverse().get(t0));
    }

    @Override
    public int a(@Nullable T t0) {
        return this.b.getId(t0);
    }

    @Nullable
    @Override
    public T a(@Nullable ResourceKey<T> resourcekey) {
        return this.bb.get(resourcekey);
    }

    @Nullable
    @Override
    public T fromId(int i) {
        return this.b.fromId(i);
    }

    public Iterator<T> iterator() {
        return this.b.iterator();
    }

    @Nullable
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        return this.c.get(minecraftkey);
    }

    @Override
    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.c.get(minecraftkey));
    }

    @Override
    public Set<MinecraftKey> keySet() {
        return Collections.unmodifiableSet(this.c.keySet());
    }

    public Set<Entry<ResourceKey<T>, T>> c() {
        return Collections.unmodifiableMap(this.bb).entrySet();
    }

    @Nullable
    public T a(Random random) {
        if (this.d == null) {
            Collection<?> collection = this.c.values();

            if (collection.isEmpty()) {
                return null;
            }

            this.d = collection.toArray(new Object[collection.size()]);
        }

        return SystemUtils.a(this.d, random);
    }

    @Override
    public boolean c(MinecraftKey minecraftkey) {
        return this.c.containsKey(minecraftkey);
    }

    @Override
    public boolean b(int i) {
        return this.b.b(i);
    }

    @Override
    public boolean c(ResourceKey<T> resourcekey) {
        return this.bc.contains(resourcekey);
    }

    @Override
    public void d(ResourceKey<T> resourcekey) {
        this.bc.add(resourcekey);
    }

    public static <T> Codec<RegistryMaterials<T>> a(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle, MapCodec<T> mapcodec) {
        return Codecs.a(resourcekey, mapcodec).codec().listOf().xmap((list) -> {
            RegistryMaterials<T> registrymaterials = new RegistryMaterials<>(resourcekey, lifecycle);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Pair<ResourceKey<T>, T> pair = (Pair) iterator.next();

                registrymaterials.a((ResourceKey) pair.getFirst(), pair.getSecond());
            }

            return registrymaterials;
        }, (registrymaterials) -> {
            Builder<Pair<ResourceKey<T>, T>> builder = ImmutableList.builder();
            Iterator iterator = registrymaterials.b.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();

                builder.add(Pair.of(registrymaterials.c(t0).get(), t0));
            }

            return builder.build();
        });
    }

    public static <T> Codec<RegistryMaterials<T>> b(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle, MapCodec<T> mapcodec) {
        return RegistryDataPackCodec.a(resourcekey, lifecycle, mapcodec);
    }

    public static <T> Codec<RegistryMaterials<T>> c(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle, MapCodec<T> mapcodec) {
        return Codec.unboundedMap(MinecraftKey.a.xmap(ResourceKey.a(resourcekey), ResourceKey::a), mapcodec.codec()).xmap((map) -> {
            RegistryMaterials<T> registrymaterials = new RegistryMaterials<>(resourcekey, lifecycle);

            map.forEach((resourcekey1, object) -> {
                registrymaterials.a(registrymaterials.bd, resourcekey1, object);
                registrymaterials.d(resourcekey1);
            });
            return registrymaterials;
        }, (registrymaterials) -> {
            com.google.common.collect.ImmutableMap.Builder<ResourceKey<T>, T> com_google_common_collect_immutablemap_builder = ImmutableMap.builder();

            registrymaterials.bb.entrySet().stream().filter((entry) -> {
                return registrymaterials.c((ResourceKey) entry.getKey());
            }).forEach(com_google_common_collect_immutablemap_builder::put);
            return com_google_common_collect_immutablemap_builder.build();
        });
    }
}

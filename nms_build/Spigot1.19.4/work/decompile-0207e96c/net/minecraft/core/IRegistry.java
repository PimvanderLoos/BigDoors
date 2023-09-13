package net.minecraft.core;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public interface IRegistry<T> extends Keyable, Registry<T> {

    ResourceKey<? extends IRegistry<T>> key();

    default Codec<T> byNameCodec() {
        Codec<T> codec = MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
            return (DataResult) Optional.ofNullable(this.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    ResourceKey resourcekey = this.key();

                    return "Unknown registry key in " + resourcekey + ": " + minecraftkey;
                });
            });
        }, (object) -> {
            return (DataResult) this.getResourceKey(object).map(ResourceKey::location).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    ResourceKey resourcekey = this.key();

                    return "Unknown registry element in " + resourcekey + ":" + object;
                });
            });
        });
        Codec<T> codec1 = ExtraCodecs.idResolverCodec((object) -> {
            return this.getResourceKey(object).isPresent() ? this.getId(object) : -1;
        }, this::byId, -1);

        return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed(codec, codec1), this::lifecycle, this::lifecycle);
    }

    default Codec<Holder<T>> holderByNameCodec() {
        Codec<Holder<T>> codec = MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
            return (DataResult) this.getHolder(ResourceKey.create(this.key(), minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    ResourceKey resourcekey = this.key();

                    return "Unknown registry key in " + resourcekey + ": " + minecraftkey;
                });
            });
        }, (holder) -> {
            return (DataResult) holder.unwrapKey().map(ResourceKey::location).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    ResourceKey resourcekey = this.key();

                    return "Unknown registry element in " + resourcekey + ":" + holder;
                });
            });
        });

        return ExtraCodecs.overrideLifecycle(codec, (holder) -> {
            return this.lifecycle(holder.value());
        }, (holder) -> {
            return this.lifecycle(holder.value());
        });
    }

    default <U> Stream<U> keys(DynamicOps<U> dynamicops) {
        return this.keySet().stream().map((minecraftkey) -> {
            return dynamicops.createString(minecraftkey.toString());
        });
    }

    @Nullable
    MinecraftKey getKey(T t0);

    Optional<ResourceKey<T>> getResourceKey(T t0);

    @Override
    int getId(@Nullable T t0);

    @Nullable
    T get(@Nullable ResourceKey<T> resourcekey);

    @Nullable
    T get(@Nullable MinecraftKey minecraftkey);

    Lifecycle lifecycle(T t0);

    Lifecycle registryLifecycle();

    default Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.get(minecraftkey));
    }

    default Optional<T> getOptional(@Nullable ResourceKey<T> resourcekey) {
        return Optional.ofNullable(this.get(resourcekey));
    }

    default T getOrThrow(ResourceKey<T> resourcekey) {
        T t0 = this.get(resourcekey);

        if (t0 == null) {
            ResourceKey resourcekey1 = this.key();

            throw new IllegalStateException("Missing key in " + resourcekey1 + ": " + resourcekey);
        } else {
            return t0;
        }
    }

    Set<MinecraftKey> keySet();

    Set<Entry<ResourceKey<T>, T>> entrySet();

    Set<ResourceKey<T>> registryKeySet();

    Optional<Holder.c<T>> getRandom(RandomSource randomsource);

    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    boolean containsKey(MinecraftKey minecraftkey);

    boolean containsKey(ResourceKey<T> resourcekey);

    static <T> T register(IRegistry<? super T> iregistry, String s, T t0) {
        return register(iregistry, new MinecraftKey(s), t0);
    }

    static <V, T extends V> T register(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return register(iregistry, ResourceKey.create(iregistry.key(), minecraftkey), t0);
    }

    static <V, T extends V> T register(IRegistry<V> iregistry, ResourceKey<V> resourcekey, T t0) {
        ((IRegistryWritable) iregistry).register(resourcekey, t0, Lifecycle.stable());
        return t0;
    }

    static <T> Holder.c<T> registerForHolder(IRegistry<T> iregistry, ResourceKey<T> resourcekey, T t0) {
        return ((IRegistryWritable) iregistry).register(resourcekey, t0, Lifecycle.stable());
    }

    static <T> Holder.c<T> registerForHolder(IRegistry<T> iregistry, MinecraftKey minecraftkey, T t0) {
        return registerForHolder(iregistry, ResourceKey.create(iregistry.key(), minecraftkey), t0);
    }

    static <V, T extends V> T registerMapping(IRegistry<V> iregistry, int i, String s, T t0) {
        ((IRegistryWritable) iregistry).registerMapping(i, ResourceKey.create(iregistry.key(), new MinecraftKey(s)), t0, Lifecycle.stable());
        return t0;
    }

    IRegistry<T> freeze();

    Holder.c<T> createIntrusiveHolder(T t0);

    Optional<Holder.c<T>> getHolder(int i);

    Optional<Holder.c<T>> getHolder(ResourceKey<T> resourcekey);

    Holder<T> wrapAsHolder(T t0);

    default Holder.c<T> getHolderOrThrow(ResourceKey<T> resourcekey) {
        return (Holder.c) this.getHolder(resourcekey).orElseThrow(() -> {
            ResourceKey resourcekey1 = this.key();

            return new IllegalStateException("Missing key in " + resourcekey1 + ": " + resourcekey);
        });
    }

    Stream<Holder.c<T>> holders();

    Optional<HolderSet.Named<T>> getTag(TagKey<T> tagkey);

    default Iterable<Holder<T>> getTagOrEmpty(TagKey<T> tagkey) {
        return (Iterable) DataFixUtils.orElse(this.getTag(tagkey), List.of());
    }

    HolderSet.Named<T> getOrCreateTag(TagKey<T> tagkey);

    Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

    Stream<TagKey<T>> getTagNames();

    void resetTags();

    void bindTags(Map<TagKey<T>, List<Holder<T>>> map);

    default Registry<Holder<T>> asHolderIdMap() {
        return new Registry<Holder<T>>() {
            public int getId(Holder<T> holder) {
                return IRegistry.this.getId(holder.value());
            }

            @Nullable
            @Override
            public Holder<T> byId(int i) {
                return (Holder) IRegistry.this.getHolder(i).orElse((Object) null);
            }

            @Override
            public int size() {
                return IRegistry.this.size();
            }

            public Iterator<Holder<T>> iterator() {
                return IRegistry.this.holders().map((holder_c) -> {
                    return holder_c;
                }).iterator();
            }
        };
    }

    HolderOwner<T> holderOwner();

    HolderLookup.c<T> asLookup();

    default HolderLookup.c<T> asTagAddingLookup() {
        return new HolderLookup.c.a<T>() {
            @Override
            protected HolderLookup.c<T> parent() {
                return IRegistry.this.asLookup();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tagkey) {
                return Optional.of(this.getOrThrow(tagkey));
            }

            @Override
            public HolderSet.Named<T> getOrThrow(TagKey<T> tagkey) {
                return IRegistry.this.getOrCreateTag(tagkey);
            }
        };
    }
}

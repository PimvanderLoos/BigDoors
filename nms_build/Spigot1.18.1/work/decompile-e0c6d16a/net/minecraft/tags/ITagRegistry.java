package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ITagRegistry {

    static final Logger LOGGER = LogManager.getLogger();
    public static final ITagRegistry EMPTY = new ITagRegistry(ImmutableMap.of());
    private final Map<ResourceKey<? extends IRegistry<?>>, Tags<?>> collections;

    ITagRegistry(Map<ResourceKey<? extends IRegistry<?>>, Tags<?>> map) {
        this.collections = map;
    }

    @Nullable
    private <T> Tags<T> get(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (Tags) this.collections.get(resourcekey);
    }

    public <T> Tags<T> getOrEmpty(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (Tags) this.collections.getOrDefault(resourcekey, Tags.empty());
    }

    public <T, E extends Exception> Tag<T> getTagOrThrow(ResourceKey<? extends IRegistry<T>> resourcekey, MinecraftKey minecraftkey, Function<MinecraftKey, E> function) throws E {
        Tags<T> tags = this.get(resourcekey);

        if (tags == null) {
            throw (Exception) function.apply(minecraftkey);
        } else {
            Tag<T> tag = tags.getTag(minecraftkey);

            if (tag == null) {
                throw (Exception) function.apply(minecraftkey);
            } else {
                return tag;
            }
        }
    }

    public <T, E extends Exception> MinecraftKey getIdOrThrow(ResourceKey<? extends IRegistry<T>> resourcekey, Tag<T> tag, Supplier<E> supplier) throws E {
        Tags<T> tags = this.get(resourcekey);

        if (tags == null) {
            throw (Exception) supplier.get();
        } else {
            MinecraftKey minecraftkey = tags.getId(tag);

            if (minecraftkey == null) {
                throw (Exception) supplier.get();
            } else {
                return minecraftkey;
            }
        }
    }

    public void getAll(ITagRegistry.b itagregistry_b) {
        this.collections.forEach((resourcekey, tags) -> {
            acceptCap(itagregistry_b, resourcekey, tags);
        });
    }

    private static <T> void acceptCap(ITagRegistry.b itagregistry_b, ResourceKey<? extends IRegistry<?>> resourcekey, Tags<?> tags) {
        itagregistry_b.accept(resourcekey, tags);
    }

    public void bindToGlobal() {
        TagStatic.resetAll(this);
        Blocks.rebuildCache();
    }

    public Map<ResourceKey<? extends IRegistry<?>>, Tags.a> serializeToNetwork(final IRegistryCustom iregistrycustom) {
        final Map<ResourceKey<? extends IRegistry<?>>, Tags.a> map = Maps.newHashMap();

        this.getAll(new ITagRegistry.b() {
            @Override
            public <T> void accept(ResourceKey<? extends IRegistry<T>> resourcekey, Tags<T> tags) {
                Optional<? extends IRegistry<T>> optional = iregistrycustom.registry(resourcekey);

                if (optional.isPresent()) {
                    map.put(resourcekey, tags.serializeToNetwork((IRegistry) optional.get()));
                } else {
                    ITagRegistry.LOGGER.error("Unknown registry {}", resourcekey);
                }

            }
        });
        return map;
    }

    public static ITagRegistry deserializeFromNetwork(IRegistryCustom iregistrycustom, Map<ResourceKey<? extends IRegistry<?>>, Tags.a> map) {
        ITagRegistry.a itagregistry_a = new ITagRegistry.a();

        map.forEach((resourcekey, tags_a) -> {
            addTagsFromPayload(iregistrycustom, itagregistry_a, resourcekey, tags_a);
        });
        return itagregistry_a.build();
    }

    private static <T> void addTagsFromPayload(IRegistryCustom iregistrycustom, ITagRegistry.a itagregistry_a, ResourceKey<? extends IRegistry<? extends T>> resourcekey, Tags.a tags_a) {
        Optional<? extends IRegistry<? extends T>> optional = iregistrycustom.registry(resourcekey);

        if (optional.isPresent()) {
            itagregistry_a.add(resourcekey, Tags.createFromNetwork(tags_a, (IRegistry) optional.get()));
        } else {
            ITagRegistry.LOGGER.error("Unknown registry {}", resourcekey);
        }

    }

    @FunctionalInterface
    interface b {

        <T> void accept(ResourceKey<? extends IRegistry<T>> resourcekey, Tags<T> tags);
    }

    public static class a {

        private final Builder<ResourceKey<? extends IRegistry<?>>, Tags<?>> result = ImmutableMap.builder();

        public a() {}

        public <T> ITagRegistry.a add(ResourceKey<? extends IRegistry<? extends T>> resourcekey, Tags<T> tags) {
            this.result.put(resourcekey, tags);
            return this;
        }

        public ITagRegistry build() {
            return new ITagRegistry(this.result.build());
        }
    }
}

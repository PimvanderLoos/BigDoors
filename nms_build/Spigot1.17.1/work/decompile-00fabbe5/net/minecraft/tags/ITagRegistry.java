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
    private <T> Tags<T> b(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (Tags) this.collections.get(resourcekey);
    }

    public <T> Tags<T> a(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return (Tags) this.collections.getOrDefault(resourcekey, Tags.c());
    }

    public <T, E extends Exception> Tag<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, MinecraftKey minecraftkey, Function<MinecraftKey, E> function) throws E {
        Tags<T> tags = this.b(resourcekey);

        if (tags == null) {
            throw (Exception) function.apply(minecraftkey);
        } else {
            Tag<T> tag = tags.a(minecraftkey);

            if (tag == null) {
                throw (Exception) function.apply(minecraftkey);
            } else {
                return tag;
            }
        }
    }

    public <T, E extends Exception> MinecraftKey a(ResourceKey<? extends IRegistry<T>> resourcekey, Tag<T> tag, Supplier<E> supplier) throws E {
        Tags<T> tags = this.b(resourcekey);

        if (tags == null) {
            throw (Exception) supplier.get();
        } else {
            MinecraftKey minecraftkey = tags.a(tag);

            if (minecraftkey == null) {
                throw (Exception) supplier.get();
            } else {
                return minecraftkey;
            }
        }
    }

    public void a(ITagRegistry.b itagregistry_b) {
        this.collections.forEach((resourcekey, tags) -> {
            a(itagregistry_b, resourcekey, tags);
        });
    }

    private static <T> void a(ITagRegistry.b itagregistry_b, ResourceKey<? extends IRegistry<?>> resourcekey, Tags<?> tags) {
        itagregistry_b.a(resourcekey, tags);
    }

    public void bind() {
        TagStatic.a(this);
        Blocks.a();
    }

    public Map<ResourceKey<? extends IRegistry<?>>, Tags.a> a(final IRegistryCustom iregistrycustom) {
        final Map<ResourceKey<? extends IRegistry<?>>, Tags.a> map = Maps.newHashMap();

        this.a(new ITagRegistry.b() {
            @Override
            public <T> void a(ResourceKey<? extends IRegistry<T>> resourcekey, Tags<T> tags) {
                Optional<? extends IRegistry<T>> optional = iregistrycustom.c(resourcekey);

                if (optional.isPresent()) {
                    map.put(resourcekey, tags.a((IRegistry) optional.get()));
                } else {
                    ITagRegistry.LOGGER.error("Unknown registry {}", resourcekey);
                }

            }
        });
        return map;
    }

    public static ITagRegistry a(IRegistryCustom iregistrycustom, Map<ResourceKey<? extends IRegistry<?>>, Tags.a> map) {
        ITagRegistry.a itagregistry_a = new ITagRegistry.a();

        map.forEach((resourcekey, tags_a) -> {
            a(iregistrycustom, itagregistry_a, resourcekey, tags_a);
        });
        return itagregistry_a.a();
    }

    private static <T> void a(IRegistryCustom iregistrycustom, ITagRegistry.a itagregistry_a, ResourceKey<? extends IRegistry<? extends T>> resourcekey, Tags.a tags_a) {
        Optional<? extends IRegistry<? extends T>> optional = iregistrycustom.c(resourcekey);

        if (optional.isPresent()) {
            itagregistry_a.a(resourcekey, Tags.a(tags_a, (IRegistry) optional.get()));
        } else {
            ITagRegistry.LOGGER.error("Unknown registry {}", resourcekey);
        }

    }

    @FunctionalInterface
    interface b {

        <T> void a(ResourceKey<? extends IRegistry<T>> resourcekey, Tags<T> tags);
    }

    public static class a {

        private final Builder<ResourceKey<? extends IRegistry<?>>, Tags<?>> result = ImmutableMap.builder();

        public a() {}

        public <T> ITagRegistry.a a(ResourceKey<? extends IRegistry<? extends T>> resourcekey, Tags<T> tags) {
            this.result.put(resourcekey, tags);
            return this;
        }

        public ITagRegistry a() {
            return new ITagRegistry(this.result.build());
        }
    }
}

package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.profiling.GameProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagRegistry implements IReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private final IRegistryCustom registryAccess;
    private ITagRegistry tags;

    public TagRegistry(IRegistryCustom iregistrycustom) {
        this.tags = ITagRegistry.EMPTY;
        this.registryAccess = iregistrycustom;
    }

    public ITagRegistry a() {
        return this.tags;
    }

    @Override
    public CompletableFuture<Void> a(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        List<TagRegistry.a<?>> list = Lists.newArrayList();

        TagStatic.a((tagutil) -> {
            TagRegistry.a<?> tagregistry_a = this.a(iresourcemanager, executor, tagutil);

            if (tagregistry_a != null) {
                list.add(tagregistry_a);
            }

        });
        CompletableFuture completablefuture = CompletableFuture.allOf((CompletableFuture[]) list.stream().map((tagregistry_a) -> {
            return tagregistry_a.pendingLoad;
        }).toArray((i) -> {
            return new CompletableFuture[i];
        }));

        Objects.requireNonNull(ireloadlistener_a);
        return completablefuture.thenCompose(ireloadlistener_a::a).thenAcceptAsync((ovoid) -> {
            ITagRegistry.a itagregistry_a = new ITagRegistry.a();

            list.forEach((tagregistry_a) -> {
                tagregistry_a.a(itagregistry_a);
            });
            ITagRegistry itagregistry = itagregistry_a.a();
            Multimap<ResourceKey<? extends IRegistry<?>>, MinecraftKey> multimap = TagStatic.b(itagregistry);

            if (!multimap.isEmpty()) {
                Stream stream = multimap.entries().stream().map((entry) -> {
                    Object object = entry.getKey();

                    return object + ":" + entry.getValue();
                }).sorted();

                throw new IllegalStateException("Missing required tags: " + (String) stream.collect(Collectors.joining(",")));
            } else {
                TagsInstance.a(itagregistry);
                this.tags = itagregistry;
            }
        }, executor1);
    }

    @Nullable
    private <T> TagRegistry.a<T> a(IResourceManager iresourcemanager, Executor executor, TagUtil<T> tagutil) {
        Optional<? extends IRegistry<T>> optional = this.registryAccess.c(tagutil.c());

        if (optional.isPresent()) {
            IRegistry<T> iregistry = (IRegistry) optional.get();

            Objects.requireNonNull(iregistry);
            TagDataPack<T> tagdatapack = new TagDataPack<>(iregistry::getOptional, tagutil.d());
            CompletableFuture<? extends Tags<T>> completablefuture = CompletableFuture.supplyAsync(() -> {
                return tagdatapack.b(iresourcemanager);
            }, executor);

            return new TagRegistry.a<>(tagutil, completablefuture);
        } else {
            TagRegistry.LOGGER.warn("Can't find registry for {}", tagutil.c());
            return null;
        }
    }

    private static class a<T> {

        private final TagUtil<T> helper;
        final CompletableFuture<? extends Tags<T>> pendingLoad;

        a(TagUtil<T> tagutil, CompletableFuture<? extends Tags<T>> completablefuture) {
            this.helper = tagutil;
            this.pendingLoad = completablefuture;
        }

        public void a(ITagRegistry.a itagregistry_a) {
            itagregistry_a.a(this.helper.c(), (Tags) this.pendingLoad.join());
        }
    }
}

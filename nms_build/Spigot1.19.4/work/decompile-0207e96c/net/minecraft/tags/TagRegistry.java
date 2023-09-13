package net.minecraft.tags;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.profiling.GameProfilerFiller;

public class TagRegistry implements IReloadListener {

    private static final Map<ResourceKey<? extends IRegistry<?>>, String> CUSTOM_REGISTRY_DIRECTORIES = Map.of(Registries.BLOCK, "tags/blocks", Registries.ENTITY_TYPE, "tags/entity_types", Registries.FLUID, "tags/fluids", Registries.GAME_EVENT, "tags/game_events", Registries.ITEM, "tags/items");
    private final IRegistryCustom registryAccess;
    private List<TagRegistry.a<?>> results = List.of();

    public TagRegistry(IRegistryCustom iregistrycustom) {
        this.registryAccess = iregistrycustom;
    }

    public List<TagRegistry.a<?>> getResult() {
        return this.results;
    }

    public static String getTagDir(ResourceKey<? extends IRegistry<?>> resourcekey) {
        String s = (String) TagRegistry.CUSTOM_REGISTRY_DIRECTORIES.get(resourcekey);

        return s != null ? s : "tags/" + resourcekey.location().getPath();
    }

    @Override
    public CompletableFuture<Void> reload(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        List<? extends CompletableFuture<? extends TagRegistry.a<?>>> list = this.registryAccess.registries().map((iregistrycustom_d) -> {
            return this.createLoader(iresourcemanager, executor, iregistrycustom_d);
        }).toList();
        CompletableFuture completablefuture = CompletableFuture.allOf((CompletableFuture[]) list.toArray((i) -> {
            return new CompletableFuture[i];
        }));

        Objects.requireNonNull(ireloadlistener_a);
        return completablefuture.thenCompose(ireloadlistener_a::wait).thenAcceptAsync((ovoid) -> {
            this.results = (List) list.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());
        }, executor1);
    }

    private <T> CompletableFuture<TagRegistry.a<T>> createLoader(IResourceManager iresourcemanager, Executor executor, IRegistryCustom.d<T> iregistrycustom_d) {
        ResourceKey<? extends IRegistry<T>> resourcekey = iregistrycustom_d.key();
        IRegistry<T> iregistry = iregistrycustom_d.value();
        TagDataPack<Holder<T>> tagdatapack = new TagDataPack<>((minecraftkey) -> {
            return iregistry.getHolder(ResourceKey.create(resourcekey, minecraftkey));
        }, getTagDir(resourcekey));

        return CompletableFuture.supplyAsync(() -> {
            return new TagRegistry.a<>(resourcekey, tagdatapack.loadAndBuild(iresourcemanager));
        }, executor);
    }

    public static record a<T> (ResourceKey<? extends IRegistry<T>> key, Map<MinecraftKey, Collection<Holder<T>>> tags) {

    }
}

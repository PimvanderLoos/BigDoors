package net.minecraft.tags;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidType;

public class TagRegistry implements IReloadListener {

    private final TagDataPack<Block> blockTags;
    private final TagDataPack<Item> itemTags;
    private final TagDataPack<FluidType> fluidTags;
    private final TagDataPack<EntityTypes<?>> entityTags;
    private ITagRegistry e;

    public TagRegistry() {
        this.blockTags = new TagDataPack<>(IRegistry.BLOCK::getOptional, "tags/blocks", "block");
        this.itemTags = new TagDataPack<>(IRegistry.ITEM::getOptional, "tags/items", "item");
        this.fluidTags = new TagDataPack<>(IRegistry.FLUID::getOptional, "tags/fluids", "fluid");
        this.entityTags = new TagDataPack<>(IRegistry.ENTITY_TYPE::getOptional, "tags/entity_types", "entity_type");
        this.e = ITagRegistry.a;
    }

    public ITagRegistry a() {
        return this.e;
    }

    @Override
    public CompletableFuture<Void> a(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        CompletableFuture<Map<MinecraftKey, Tag.a>> completablefuture = this.blockTags.a(iresourcemanager, executor);
        CompletableFuture<Map<MinecraftKey, Tag.a>> completablefuture1 = this.itemTags.a(iresourcemanager, executor);
        CompletableFuture<Map<MinecraftKey, Tag.a>> completablefuture2 = this.fluidTags.a(iresourcemanager, executor);
        CompletableFuture<Map<MinecraftKey, Tag.a>> completablefuture3 = this.entityTags.a(iresourcemanager, executor);
        CompletableFuture completablefuture4 = CompletableFuture.allOf(completablefuture, completablefuture1, completablefuture2, completablefuture3);

        ireloadlistener_a.getClass();
        return completablefuture4.thenCompose(ireloadlistener_a::a).thenAcceptAsync((ovoid) -> {
            Tags<Block> tags = this.blockTags.a((Map) completablefuture.join());
            Tags<Item> tags1 = this.itemTags.a((Map) completablefuture1.join());
            Tags<FluidType> tags2 = this.fluidTags.a((Map) completablefuture2.join());
            Tags<EntityTypes<?>> tags3 = this.entityTags.a((Map) completablefuture3.join());
            ITagRegistry itagregistry = ITagRegistry.a(tags, tags1, tags2, tags3);
            Multimap<MinecraftKey, MinecraftKey> multimap = TagStatic.b(itagregistry);

            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + (String) multimap.entries().stream().map((entry) -> {
                    return entry.getKey() + ":" + entry.getValue();
                }).sorted().collect(Collectors.joining(",")));
            } else {
                TagsInstance.a(itagregistry);
                this.e = itagregistry;
            }
        }, executor1);
    }
}

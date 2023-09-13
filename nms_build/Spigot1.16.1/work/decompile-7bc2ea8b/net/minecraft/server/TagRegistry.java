package net.minecraft.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class TagRegistry implements IReloadListener {

    private final TagsServer<Block> blockTags;
    private final TagsServer<Item> itemTags;
    private final TagsServer<FluidType> fluidTags;
    private final TagsServer<EntityTypes<?>> entityTags;

    public TagRegistry() {
        this.blockTags = new TagsServer<>(IRegistry.BLOCK, "tags/blocks", "block");
        this.itemTags = new TagsServer<>(IRegistry.ITEM, "tags/items", "item");
        this.fluidTags = new TagsServer<>(IRegistry.FLUID, "tags/fluids", "fluid");
        this.entityTags = new TagsServer<>(IRegistry.ENTITY_TYPE, "tags/entity_types", "entity_type");
    }

    public TagsServer<Block> getBlockTags() {
        return this.blockTags;
    }

    public TagsServer<Item> getItemTags() {
        return this.itemTags;
    }

    public TagsServer<FluidType> getFluidTags() {
        return this.fluidTags;
    }

    public TagsServer<EntityTypes<?>> getEntityTags() {
        return this.entityTags;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.blockTags.a(packetdataserializer);
        this.itemTags.a(packetdataserializer);
        this.fluidTags.a(packetdataserializer);
        this.entityTags.a(packetdataserializer);
    }

    public static TagRegistry b(PacketDataSerializer packetdataserializer) {
        TagRegistry tagregistry = new TagRegistry();

        tagregistry.getBlockTags().b(packetdataserializer);
        tagregistry.getItemTags().b(packetdataserializer);
        tagregistry.getFluidTags().b(packetdataserializer);
        tagregistry.getEntityTags().b(packetdataserializer);
        return tagregistry;
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
            this.blockTags.a((Map) completablefuture.join());
            this.itemTags.a((Map) completablefuture1.join());
            this.fluidTags.a((Map) completablefuture2.join());
            this.entityTags.a((Map) completablefuture3.join());
            TagsInstance.a(this.blockTags, this.itemTags, this.fluidTags, this.entityTags);
            Multimap<String, MinecraftKey> multimap = HashMultimap.create();

            multimap.putAll("blocks", TagsBlock.b(this.blockTags));
            multimap.putAll("items", TagsItem.b(this.itemTags));
            multimap.putAll("fluids", TagsFluid.b(this.fluidTags));
            multimap.putAll("entity_types", TagsEntity.b(this.entityTags));
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + (String) multimap.entries().stream().map((entry) -> {
                    return (String) entry.getKey() + ":" + entry.getValue();
                }).sorted().collect(Collectors.joining(",")));
            }
        }, executor1);
    }

    public void bind() {
        TagsBlock.a((Tags) this.blockTags);
        TagsItem.a((Tags) this.itemTags);
        TagsFluid.a((Tags) this.fluidTags);
        TagsEntity.a((Tags) this.entityTags);
        Blocks.a();
    }
}

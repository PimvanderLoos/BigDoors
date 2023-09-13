package net.minecraft.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IReloadableResourceManager;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.ITagRegistry;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.LootTableRegistry;

public class DataPackResources implements AutoCloseable {

    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final IReloadableResourceManager resources;
    public CommandDispatcher commands;
    private final CraftingManager recipes;
    private final TagRegistry tagManager;
    private final LootPredicateManager predicateManager;
    private final LootTableRegistry lootTables;
    private final ItemModifierManager itemModifierManager;
    private final AdvancementDataWorld advancements;
    private final CustomFunctionManager functionLibrary;

    public DataPackResources(IRegistryCustom iregistrycustom, CommandDispatcher.ServerType commanddispatcher_servertype, int i) {
        this.resources = new ResourceManager(EnumResourcePackType.SERVER_DATA);
        this.recipes = new CraftingManager();
        this.predicateManager = new LootPredicateManager();
        this.lootTables = new LootTableRegistry(this.predicateManager);
        this.itemModifierManager = new ItemModifierManager(this.predicateManager, this.lootTables);
        this.advancements = new AdvancementDataWorld(this.predicateManager);
        this.tagManager = new TagRegistry(iregistrycustom);
        this.commands = new CommandDispatcher(commanddispatcher_servertype);
        this.functionLibrary = new CustomFunctionManager(i, this.commands.a());
        this.resources.a((IReloadListener) this.tagManager);
        this.resources.a((IReloadListener) this.predicateManager);
        this.resources.a((IReloadListener) this.recipes);
        this.resources.a((IReloadListener) this.lootTables);
        this.resources.a((IReloadListener) this.itemModifierManager);
        this.resources.a((IReloadListener) this.functionLibrary);
        this.resources.a((IReloadListener) this.advancements);
    }

    public CustomFunctionManager a() {
        return this.functionLibrary;
    }

    public LootPredicateManager b() {
        return this.predicateManager;
    }

    public LootTableRegistry c() {
        return this.lootTables;
    }

    public ItemModifierManager d() {
        return this.itemModifierManager;
    }

    public ITagRegistry e() {
        return this.tagManager.a();
    }

    public CraftingManager f() {
        return this.recipes;
    }

    public CommandDispatcher g() {
        return this.commands;
    }

    public AdvancementDataWorld h() {
        return this.advancements;
    }

    public IResourceManager i() {
        return this.resources;
    }

    public static CompletableFuture<DataPackResources> a(List<IResourcePack> list, IRegistryCustom iregistrycustom, CommandDispatcher.ServerType commanddispatcher_servertype, int i, Executor executor, Executor executor1) {
        DataPackResources datapackresources = new DataPackResources(iregistrycustom, commanddispatcher_servertype, i);
        CompletableFuture<Unit> completablefuture = datapackresources.resources.a(executor, executor1, list, DataPackResources.DATA_RELOAD_INITIAL_TASK);

        return completablefuture.whenComplete((unit, throwable) -> {
            if (throwable != null) {
                datapackresources.close();
            }

        }).thenApply((unit) -> {
            return datapackresources;
        });
    }

    public void j() {
        this.tagManager.a().bind();
    }

    public void close() {
        this.resources.close();
    }
}

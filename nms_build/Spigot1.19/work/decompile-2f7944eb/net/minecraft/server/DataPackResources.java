package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.IReloadListener;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.Reloadable;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import net.minecraft.world.level.storage.loot.LootTableRegistry;
import org.slf4j.Logger;

public class DataPackResources {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final CommandBuildContext commandBuildContext;
    public CommandDispatcher commands;
    private final CraftingManager recipes = new CraftingManager();
    private final TagRegistry tagManager;
    private final LootPredicateManager predicateManager = new LootPredicateManager();
    private final LootTableRegistry lootTables;
    private final ItemModifierManager itemModifierManager;
    private final AdvancementDataWorld advancements;
    private final CustomFunctionManager functionLibrary;

    public DataPackResources(IRegistryCustom.Dimension iregistrycustom_dimension, CommandDispatcher.ServerType commanddispatcher_servertype, int i) {
        this.lootTables = new LootTableRegistry(this.predicateManager);
        this.itemModifierManager = new ItemModifierManager(this.predicateManager, this.lootTables);
        this.advancements = new AdvancementDataWorld(this.predicateManager);
        this.tagManager = new TagRegistry(iregistrycustom_dimension);
        this.commandBuildContext = new CommandBuildContext(iregistrycustom_dimension);
        this.commands = new CommandDispatcher(commanddispatcher_servertype, this.commandBuildContext);
        this.commandBuildContext.missingTagAccessPolicy(CommandBuildContext.a.CREATE_NEW);
        this.functionLibrary = new CustomFunctionManager(i, this.commands.getDispatcher());
    }

    public CustomFunctionManager getFunctionLibrary() {
        return this.functionLibrary;
    }

    public LootPredicateManager getPredicateManager() {
        return this.predicateManager;
    }

    public LootTableRegistry getLootTables() {
        return this.lootTables;
    }

    public ItemModifierManager getItemModifierManager() {
        return this.itemModifierManager;
    }

    public CraftingManager getRecipeManager() {
        return this.recipes;
    }

    public CommandDispatcher getCommands() {
        return this.commands;
    }

    public AdvancementDataWorld getAdvancements() {
        return this.advancements;
    }

    public List<IReloadListener> listeners() {
        return List.of(this.tagManager, this.predicateManager, this.recipes, this.lootTables, this.itemModifierManager, this.functionLibrary, this.advancements);
    }

    public static CompletableFuture<DataPackResources> loadResources(IResourceManager iresourcemanager, IRegistryCustom.Dimension iregistrycustom_dimension, CommandDispatcher.ServerType commanddispatcher_servertype, int i, Executor executor, Executor executor1) {
        DataPackResources datapackresources = new DataPackResources(iregistrycustom_dimension, commanddispatcher_servertype, i);

        return Reloadable.create(iresourcemanager, datapackresources.listeners(), executor, executor1, DataPackResources.DATA_RELOAD_INITIAL_TASK, DataPackResources.LOGGER.isDebugEnabled()).done().whenComplete((object, throwable) -> {
            datapackresources.commandBuildContext.missingTagAccessPolicy(CommandBuildContext.a.FAIL);
        }).thenApply((object) -> {
            return datapackresources;
        });
    }

    public void updateRegistryTags(IRegistryCustom iregistrycustom) {
        this.tagManager.getResult().forEach((tagregistry_a) -> {
            updateRegistryTags(iregistrycustom, tagregistry_a);
        });
        Blocks.rebuildCache();
    }

    private static <T> void updateRegistryTags(IRegistryCustom iregistrycustom, TagRegistry.a<T> tagregistry_a) {
        ResourceKey<? extends IRegistry<T>> resourcekey = tagregistry_a.key();
        Map<TagKey<T>, List<Holder<T>>> map = (Map) tagregistry_a.tags().entrySet().stream().collect(Collectors.toUnmodifiableMap((entry) -> {
            return TagKey.create(resourcekey, (MinecraftKey) entry.getKey());
        }, (entry) -> {
            return List.copyOf((Collection) entry.getValue());
        }));

        iregistrycustom.registryOrThrow(resourcekey).bindTags(map);
    }
}

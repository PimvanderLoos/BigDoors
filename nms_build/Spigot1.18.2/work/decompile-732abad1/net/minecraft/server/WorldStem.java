package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.repository.ResourcePackRepository;
import net.minecraft.server.packs.resources.IReloadableResourceManager;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.SaveData;

public record WorldStem(IReloadableResourceManager a, DataPackResources b, IRegistryCustom.Dimension c, SaveData d) implements AutoCloseable {

    private final IReloadableResourceManager resourceManager;
    private final DataPackResources dataPackResources;
    private final IRegistryCustom.Dimension registryAccess;
    private final SaveData worldData;

    public WorldStem(IReloadableResourceManager ireloadableresourcemanager, DataPackResources datapackresources, IRegistryCustom.Dimension iregistrycustom_dimension, SaveData savedata) {
        this.resourceManager = ireloadableresourcemanager;
        this.dataPackResources = datapackresources;
        this.registryAccess = iregistrycustom_dimension;
        this.worldData = savedata;
    }

    public static CompletableFuture<WorldStem> load(WorldStem.b worldstem_b, WorldStem.a worldstem_a, WorldStem.c worldstem_c, Executor executor, Executor executor1) {
        try {
            DataPackConfiguration datapackconfiguration = (DataPackConfiguration) worldstem_a.get();
            DataPackConfiguration datapackconfiguration1 = MinecraftServer.configurePackRepository(worldstem_b.packRepository(), datapackconfiguration, worldstem_b.safeMode());
            List<IResourcePack> list = worldstem_b.packRepository().openAllSelected();
            ResourceManager resourcemanager = new ResourceManager(EnumResourcePackType.SERVER_DATA, list);
            Pair<SaveData, IRegistryCustom.Dimension> pair = worldstem_c.get(resourcemanager, datapackconfiguration1);
            SaveData savedata = (SaveData) pair.getFirst();
            IRegistryCustom.Dimension iregistrycustom_dimension = (IRegistryCustom.Dimension) pair.getSecond();

            return DataPackResources.loadResources(resourcemanager, iregistrycustom_dimension, worldstem_b.commandSelection(), worldstem_b.functionCompilationLevel(), executor, executor1).whenComplete((datapackresources, throwable) -> {
                if (throwable != null) {
                    resourcemanager.close();
                }

            }).thenApply((datapackresources) -> {
                return new WorldStem(resourcemanager, datapackresources, iregistrycustom_dimension, savedata);
            });
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    public void close() {
        this.resourceManager.close();
    }

    public void updateGlobals() {
        this.dataPackResources.updateRegistryTags(this.registryAccess);
    }

    public IReloadableResourceManager resourceManager() {
        return this.resourceManager;
    }

    public DataPackResources dataPackResources() {
        return this.dataPackResources;
    }

    public IRegistryCustom.Dimension registryAccess() {
        return this.registryAccess;
    }

    public SaveData worldData() {
        return this.worldData;
    }

    @FunctionalInterface
    public interface a extends Supplier<DataPackConfiguration> {

        static WorldStem.a loadFromWorld(Convertable.ConversionSession convertable_conversionsession) {
            return () -> {
                DataPackConfiguration datapackconfiguration = convertable_conversionsession.getDataPacks();

                if (datapackconfiguration == null) {
                    throw new IllegalStateException("Failed to load data pack config");
                } else {
                    return datapackconfiguration;
                }
            };
        }
    }

    public static record b(ResourcePackRepository a, CommandDispatcher.ServerType b, int c, boolean d) {

        private final ResourcePackRepository packRepository;
        private final CommandDispatcher.ServerType commandSelection;
        private final int functionCompilationLevel;
        private final boolean safeMode;

        public b(ResourcePackRepository resourcepackrepository, CommandDispatcher.ServerType commanddispatcher_servertype, int i, boolean flag) {
            this.packRepository = resourcepackrepository;
            this.commandSelection = commanddispatcher_servertype;
            this.functionCompilationLevel = i;
            this.safeMode = flag;
        }

        public ResourcePackRepository packRepository() {
            return this.packRepository;
        }

        public CommandDispatcher.ServerType commandSelection() {
            return this.commandSelection;
        }

        public int functionCompilationLevel() {
            return this.functionCompilationLevel;
        }

        public boolean safeMode() {
            return this.safeMode;
        }
    }

    @FunctionalInterface
    public interface c {

        Pair<SaveData, IRegistryCustom.Dimension> get(IResourceManager iresourcemanager, DataPackConfiguration datapackconfiguration);

        static WorldStem.c loadFromWorld(Convertable.ConversionSession convertable_conversionsession) {
            return (iresourcemanager, datapackconfiguration) -> {
                IRegistryCustom.e iregistrycustom_e = IRegistryCustom.builtinCopy();
                DynamicOps<NBTBase> dynamicops = RegistryOps.createAndLoad(DynamicOpsNBT.INSTANCE, iregistrycustom_e, iresourcemanager);
                SaveData savedata = convertable_conversionsession.getDataTag(dynamicops, datapackconfiguration, iregistrycustom_e.allElementsLifecycle());

                if (savedata == null) {
                    throw new IllegalStateException("Failed to load world");
                } else {
                    return Pair.of(savedata, iregistrycustom_e.freeze());
                }
            };
        }
    }
}

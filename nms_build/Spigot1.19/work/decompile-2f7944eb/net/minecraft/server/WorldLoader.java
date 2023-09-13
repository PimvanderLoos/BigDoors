package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.repository.ResourcePackRepository;
import net.minecraft.server.packs.resources.IReloadableResourceManager;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfiguration;

public class WorldLoader {

    public WorldLoader() {}

    public static <D, R> CompletableFuture<R> load(WorldLoader.a worldloader_a, WorldLoader.d<D> worldloader_d, WorldLoader.c<D, R> worldloader_c, Executor executor, Executor executor1) {
        try {
            Pair<DataPackConfiguration, IReloadableResourceManager> pair = worldloader_a.packConfig.createResourceManager();
            IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager) pair.getSecond();
            Pair<D, IRegistryCustom.Dimension> pair1 = worldloader_d.get(ireloadableresourcemanager, (DataPackConfiguration) pair.getFirst());
            D d0 = pair1.getFirst();
            IRegistryCustom.Dimension iregistrycustom_dimension = (IRegistryCustom.Dimension) pair1.getSecond();

            return DataPackResources.loadResources(ireloadableresourcemanager, iregistrycustom_dimension, worldloader_a.commandSelection(), worldloader_a.functionCompilationLevel(), executor, executor1).whenComplete((datapackresources, throwable) -> {
                if (throwable != null) {
                    ireloadableresourcemanager.close();
                }

            }).thenApplyAsync((datapackresources) -> {
                datapackresources.updateRegistryTags(iregistrycustom_dimension);
                return worldloader_c.create(ireloadableresourcemanager, datapackresources, iregistrycustom_dimension, d0);
            }, executor1);
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    public static record a(WorldLoader.b packConfig, CommandDispatcher.ServerType commandSelection, int functionCompilationLevel) {

    }

    public static record b(ResourcePackRepository packRepository, DataPackConfiguration initialDataPacks, boolean safeMode) {

        public Pair<DataPackConfiguration, IReloadableResourceManager> createResourceManager() {
            DataPackConfiguration datapackconfiguration = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataPacks, this.safeMode);
            List<IResourcePack> list = this.packRepository.openAllSelected();
            ResourceManager resourcemanager = new ResourceManager(EnumResourcePackType.SERVER_DATA, list);

            return Pair.of(datapackconfiguration, resourcemanager);
        }
    }

    @FunctionalInterface
    public interface d<D> {

        Pair<D, IRegistryCustom.Dimension> get(IResourceManager iresourcemanager, DataPackConfiguration datapackconfiguration);
    }

    @FunctionalInterface
    public interface c<D, R> {

        R create(IReloadableResourceManager ireloadableresourcemanager, DataPackResources datapackresources, IRegistryCustom.Dimension iregistrycustom_dimension, D d0);
    }
}

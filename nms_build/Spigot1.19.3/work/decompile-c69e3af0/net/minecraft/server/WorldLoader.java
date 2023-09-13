package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.repository.ResourcePackRepository;
import net.minecraft.server.packs.resources.IReloadableResourceManager;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class WorldLoader {

    private static final Logger LOGGER = LogUtils.getLogger();

    public WorldLoader() {}

    public static <D, R> CompletableFuture<R> load(WorldLoader.c worldloader_c, WorldLoader.f<D> worldloader_f, WorldLoader.e<D, R> worldloader_e, Executor executor, Executor executor1) {
        try {
            Pair<WorldDataConfiguration, IReloadableResourceManager> pair = worldloader_c.packConfig.createResourceManager();
            IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager) pair.getSecond();
            LayeredRegistryAccess<RegistryLayer> layeredregistryaccess = RegistryLayer.createRegistryAccess();
            LayeredRegistryAccess<RegistryLayer> layeredregistryaccess1 = loadAndReplaceLayer(ireloadableresourcemanager, layeredregistryaccess, RegistryLayer.WORLDGEN, RegistryDataLoader.WORLDGEN_REGISTRIES);
            IRegistryCustom.Dimension iregistrycustom_dimension = layeredregistryaccess1.getAccessForLoading(RegistryLayer.DIMENSIONS);
            IRegistryCustom.Dimension iregistrycustom_dimension1 = RegistryDataLoader.load(ireloadableresourcemanager, iregistrycustom_dimension, RegistryDataLoader.DIMENSION_REGISTRIES);
            WorldDataConfiguration worlddataconfiguration = (WorldDataConfiguration) pair.getFirst();
            WorldLoader.b<D> worldloader_b = worldloader_f.get(new WorldLoader.a(ireloadableresourcemanager, worlddataconfiguration, iregistrycustom_dimension, iregistrycustom_dimension1));
            LayeredRegistryAccess<RegistryLayer> layeredregistryaccess2 = layeredregistryaccess1.replaceFrom(RegistryLayer.DIMENSIONS, worldloader_b.finalDimensions);
            IRegistryCustom.Dimension iregistrycustom_dimension2 = layeredregistryaccess2.getAccessForLoading(RegistryLayer.RELOADABLE);

            return DataPackResources.loadResources(ireloadableresourcemanager, iregistrycustom_dimension2, worlddataconfiguration.enabledFeatures(), worldloader_c.commandSelection(), worldloader_c.functionCompilationLevel(), executor, executor1).whenComplete((datapackresources, throwable) -> {
                if (throwable != null) {
                    ireloadableresourcemanager.close();
                }

            }).thenApplyAsync((datapackresources) -> {
                datapackresources.updateRegistryTags(iregistrycustom_dimension2);
                return worldloader_e.create(ireloadableresourcemanager, datapackresources, layeredregistryaccess2, worldloader_b.cookie);
            }, executor1);
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    private static IRegistryCustom.Dimension loadLayer(IResourceManager iresourcemanager, LayeredRegistryAccess<RegistryLayer> layeredregistryaccess, RegistryLayer registrylayer, List<RegistryDataLoader.b<?>> list) {
        IRegistryCustom.Dimension iregistrycustom_dimension = layeredregistryaccess.getAccessForLoading(registrylayer);

        return RegistryDataLoader.load(iresourcemanager, iregistrycustom_dimension, list);
    }

    public static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(IResourceManager iresourcemanager, LayeredRegistryAccess<RegistryLayer> layeredregistryaccess, RegistryLayer registrylayer, List<RegistryDataLoader.b<?>> list) {
        IRegistryCustom.Dimension iregistrycustom_dimension = loadLayer(iresourcemanager, layeredregistryaccess, registrylayer, list);

        return layeredregistryaccess.replaceFrom(registrylayer, iregistrycustom_dimension);
    }

    public static record c(WorldLoader.d packConfig, CommandDispatcher.ServerType commandSelection, int functionCompilationLevel) {

    }

    public static record d(ResourcePackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {

        public Pair<WorldDataConfiguration, IReloadableResourceManager> createResourceManager() {
            FeatureFlagSet featureflagset = this.initMode ? FeatureFlags.REGISTRY.allFlags() : this.initialDataConfig.enabledFeatures();
            WorldDataConfiguration worlddataconfiguration = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig.dataPacks(), this.safeMode, featureflagset);

            if (!this.initMode) {
                worlddataconfiguration = worlddataconfiguration.expandFeatures(this.initialDataConfig.enabledFeatures());
            }

            List<IResourcePack> list = this.packRepository.openAllSelected();
            ResourceManager resourcemanager = new ResourceManager(EnumResourcePackType.SERVER_DATA, list);

            return Pair.of(worlddataconfiguration, resourcemanager);
        }
    }

    public static record a(IResourceManager resources, WorldDataConfiguration dataConfiguration, IRegistryCustom.Dimension datapackWorldgen, IRegistryCustom.Dimension datapackDimensions) {

    }

    @FunctionalInterface
    public interface f<D> {

        WorldLoader.b<D> get(WorldLoader.a worldloader_a);
    }

    public static record b<D> (D cookie, IRegistryCustom.Dimension finalDimensions) {

    }

    @FunctionalInterface
    public interface e<D, R> {

        R create(IReloadableResourceManager ireloadableresourcemanager, DataPackResources datapackresources, LayeredRegistryAccess<RegistryLayer> layeredregistryaccess, D d0);
    }
}

package net.minecraft.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.packs.resources.IReloadableResourceManager;
import net.minecraft.world.level.storage.SaveData;

public record WorldStem(IReloadableResourceManager resourceManager, DataPackResources dataPackResources, IRegistryCustom.Dimension registryAccess, SaveData worldData) implements AutoCloseable {

    public static CompletableFuture<WorldStem> load(WorldLoader.a worldloader_a, WorldLoader.d<SaveData> worldloader_d, Executor executor, Executor executor1) {
        return WorldLoader.load(worldloader_a, worldloader_d, WorldStem::new, executor, executor1);
    }

    public void close() {
        this.resourceManager.close();
    }
}

package net.minecraft.server;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.packs.resources.IReloadableResourceManager;
import net.minecraft.world.level.storage.SaveData;

public record WorldStem(IReloadableResourceManager resourceManager, DataPackResources dataPackResources, LayeredRegistryAccess<RegistryLayer> registries, SaveData worldData) implements AutoCloseable {

    public void close() {
        this.resourceManager.close();
    }
}

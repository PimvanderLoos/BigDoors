package net.minecraft.server.packs.repository;

import java.util.function.Consumer;

public interface ResourcePackSource {

    void loadPacks(Consumer<ResourcePackLoader> consumer);
}

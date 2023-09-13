package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.util.Unit;

public interface IReloadableResourceManager extends IResourceManager, AutoCloseable {

    default CompletableFuture<Unit> reload(Executor executor, Executor executor1, List<IResourcePack> list, CompletableFuture<Unit> completablefuture) {
        return this.createReload(executor, executor1, completablefuture, list).done();
    }

    IReloadable createReload(Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, List<IResourcePack> list);

    void registerReloadListener(IReloadListener ireloadlistener);

    void close();
}

package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.util.Unit;

public interface IReloadableResourceManager extends IResourceManager, AutoCloseable {

    default CompletableFuture<Unit> a(Executor executor, Executor executor1, List<IResourcePack> list, CompletableFuture<Unit> completablefuture) {
        return this.a(executor, executor1, completablefuture, list).a();
    }

    IReloadable a(Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, List<IResourcePack> list);

    void a(IReloadListener ireloadlistener);

    void close();
}

package net.minecraft.server.packs.resources;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.GameProfilerFiller;

public abstract class ResourceDataAbstract<T> implements IReloadListener {

    public ResourceDataAbstract() {}

    @Override
    public final CompletableFuture<Void> reload(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller, GameProfilerFiller gameprofilerfiller1, Executor executor, Executor executor1) {
        CompletableFuture completablefuture = CompletableFuture.supplyAsync(() -> {
            return this.prepare(iresourcemanager, gameprofilerfiller);
        }, executor);

        Objects.requireNonNull(ireloadlistener_a);
        return completablefuture.thenCompose(ireloadlistener_a::wait).thenAcceptAsync((object) -> {
            this.apply(object, iresourcemanager, gameprofilerfiller1);
        }, executor1);
    }

    protected abstract T prepare(IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller);

    protected abstract void apply(T t0, IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller);
}

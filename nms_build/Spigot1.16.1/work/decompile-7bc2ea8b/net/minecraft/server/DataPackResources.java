package net.minecraft.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataPackResources implements AutoCloseable {

    private static final CompletableFuture<Unit> a = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final IReloadableResourceManager b;
    public CommandDispatcher commandDispatcher;
    private final CraftingManager d;
    private final TagRegistry e;
    private final LootPredicateManager f;
    private final LootTableRegistry g;
    private final AdvancementDataWorld h;
    private final CustomFunctionManager i;

    public DataPackResources(CommandDispatcher.ServerType commanddispatcher_servertype, int i) {
        this.b = new ResourceManager(EnumResourcePackType.SERVER_DATA);
        this.d = new CraftingManager();
        this.e = new TagRegistry();
        this.f = new LootPredicateManager();
        this.g = new LootTableRegistry(this.f);
        this.h = new AdvancementDataWorld(this.f);
        this.commandDispatcher = new CommandDispatcher(commanddispatcher_servertype);
        this.i = new CustomFunctionManager(i, this.commandDispatcher.a());
        this.b.a((IReloadListener) this.e);
        this.b.a((IReloadListener) this.f);
        this.b.a((IReloadListener) this.d);
        this.b.a((IReloadListener) this.g);
        this.b.a((IReloadListener) this.i);
        this.b.a((IReloadListener) this.h);
    }

    public CustomFunctionManager a() {
        return this.i;
    }

    public LootPredicateManager b() {
        return this.f;
    }

    public LootTableRegistry c() {
        return this.g;
    }

    public TagRegistry d() {
        return this.e;
    }

    public CraftingManager e() {
        return this.d;
    }

    public CommandDispatcher f() {
        return this.commandDispatcher;
    }

    public AdvancementDataWorld g() {
        return this.h;
    }

    public IResourceManager h() {
        return this.b;
    }

    public static CompletableFuture<DataPackResources> a(List<IResourcePack> list, CommandDispatcher.ServerType commanddispatcher_servertype, int i, Executor executor, Executor executor1) {
        DataPackResources datapackresources = new DataPackResources(commanddispatcher_servertype, i);
        CompletableFuture<Unit> completablefuture = datapackresources.b.a(executor, executor1, list, DataPackResources.a);

        return completablefuture.whenComplete((unit, throwable) -> {
            if (throwable != null) {
                datapackresources.close();
            }

        }).thenApply((unit) -> {
            return datapackresources;
        });
    }

    public void i() {
        this.e.bind();
    }

    public void close() {
        this.b.close();
    }
}

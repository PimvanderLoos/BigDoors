package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SystemUtils;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.GameProfilerDisabled;

public class Reloadable<S> implements IReloadable {

    private static final int PREPARATION_PROGRESS_WEIGHT = 2;
    private static final int EXTRA_RELOAD_PROGRESS_WEIGHT = 2;
    private static final int LISTENER_PROGRESS_WEIGHT = 1;
    protected final CompletableFuture<Unit> allPreparations = new CompletableFuture();
    protected CompletableFuture<List<S>> allDone;
    final Set<IReloadListener> preparingListeners;
    private final int listenerCount;
    private int startedReloads;
    private int finishedReloads;
    private final AtomicInteger startedTaskCounter = new AtomicInteger();
    private final AtomicInteger doneTaskCounter = new AtomicInteger();

    public static Reloadable<Void> of(IResourceManager iresourcemanager, List<IReloadListener> list, Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture) {
        return new Reloadable<>(executor, executor1, iresourcemanager, list, (ireloadlistener_a, iresourcemanager1, ireloadlistener, executor2, executor3) -> {
            return ireloadlistener.reload(ireloadlistener_a, iresourcemanager1, GameProfilerDisabled.INSTANCE, GameProfilerDisabled.INSTANCE, executor, executor3);
        }, completablefuture);
    }

    protected Reloadable(Executor executor, final Executor executor1, IResourceManager iresourcemanager, List<IReloadListener> list, Reloadable.a<S> reloadable_a, CompletableFuture<Unit> completablefuture) {
        this.listenerCount = list.size();
        this.startedTaskCounter.incrementAndGet();
        AtomicInteger atomicinteger = this.doneTaskCounter;

        Objects.requireNonNull(this.doneTaskCounter);
        completablefuture.thenRun(atomicinteger::incrementAndGet);
        List<CompletableFuture<S>> list1 = Lists.newArrayList();
        final CompletableFuture<?> completablefuture1 = completablefuture;

        this.preparingListeners = Sets.newHashSet(list);

        CompletableFuture completablefuture2;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); completablefuture1 = completablefuture2) {
            final IReloadListener ireloadlistener = (IReloadListener) iterator.next();

            completablefuture2 = reloadable_a.create(new IReloadListener.a() {
                @Override
                public <T> CompletableFuture<T> wait(T t0) {
                    executor1.execute(() -> {
                        Reloadable.this.preparingListeners.remove(ireloadlistener);
                        if (Reloadable.this.preparingListeners.isEmpty()) {
                            Reloadable.this.allPreparations.complete(Unit.INSTANCE);
                        }

                    });
                    return Reloadable.this.allPreparations.thenCombine(completablefuture1, (unit, object) -> {
                        return t0;
                    });
                }
            }, iresourcemanager, ireloadlistener, (runnable) -> {
                this.startedTaskCounter.incrementAndGet();
                executor.execute(() -> {
                    runnable.run();
                    this.doneTaskCounter.incrementAndGet();
                });
            }, (runnable) -> {
                ++this.startedReloads;
                executor1.execute(() -> {
                    runnable.run();
                    ++this.finishedReloads;
                });
            });
            list1.add(completablefuture2);
        }

        this.allDone = SystemUtils.sequenceFailFast(list1);
    }

    @Override
    public CompletableFuture<?> done() {
        return this.allDone;
    }

    @Override
    public float getActualProgress() {
        int i = this.listenerCount - this.preparingListeners.size();
        float f = (float) (this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + i * 1);
        float f1 = (float) (this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount * 1);

        return f / f1;
    }

    public static IReloadable create(IResourceManager iresourcemanager, List<IReloadListener> list, Executor executor, Executor executor1, CompletableFuture<Unit> completablefuture, boolean flag) {
        return (IReloadable) (flag ? new ReloadableProfiled(iresourcemanager, list, executor, executor1, completablefuture) : of(iresourcemanager, list, executor, executor1, completablefuture));
    }

    protected interface a<S> {

        CompletableFuture<S> create(IReloadListener.a ireloadlistener_a, IResourceManager iresourcemanager, IReloadListener ireloadlistener, Executor executor, Executor executor1);
    }
}

package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IAsyncTaskHandler<R extends Runnable> implements ProfilerMeasured, Mailbox<R>, Executor {

    private final String name;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
    private int blockingCount;

    protected IAsyncTaskHandler(String s) {
        this.name = s;
        MetricsRegistry.INSTANCE.a((ProfilerMeasured) this);
    }

    protected abstract R postToMainThread(Runnable runnable);

    protected abstract boolean canExecute(R r0);

    public boolean isMainThread() {
        return Thread.currentThread() == this.getThread();
    }

    protected abstract Thread getThread();

    protected boolean isNotMainThread() {
        return !this.isMainThread();
    }

    public int bm() {
        return this.pendingRunnables.size();
    }

    @Override
    public String bn() {
        return this.name;
    }

    public <V> CompletableFuture<V> a(Supplier<V> supplier) {
        return this.isNotMainThread() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
    }

    private CompletableFuture<Void> executeFuture(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> f(Runnable runnable) {
        if (this.isNotMainThread()) {
            return this.executeFuture(runnable);
        } else {
            runnable.run();
            return CompletableFuture.completedFuture((Object) null);
        }
    }

    public void executeSync(Runnable runnable) {
        if (!this.isMainThread()) {
            this.executeFuture(runnable).join();
        } else {
            runnable.run();
        }

    }

    public void a(R r0) {
        this.pendingRunnables.add(r0);
        LockSupport.unpark(this.getThread());
    }

    public void execute(Runnable runnable) {
        if (this.isNotMainThread()) {
            this.a(this.postToMainThread(runnable));
        } else {
            runnable.run();
        }

    }

    protected void bo() {
        this.pendingRunnables.clear();
    }

    protected void executeAll() {
        while (this.executeNext()) {
            ;
        }

    }

    public boolean executeNext() {
        R r0 = (Runnable) this.pendingRunnables.peek();

        if (r0 == null) {
            return false;
        } else if (this.blockingCount == 0 && !this.canExecute(r0)) {
            return false;
        } else {
            this.executeTask((Runnable) this.pendingRunnables.remove());
            return true;
        }
    }

    public void awaitTasks(BooleanSupplier booleansupplier) {
        ++this.blockingCount;

        try {
            while (!booleansupplier.getAsBoolean()) {
                if (!this.executeNext()) {
                    this.bq();
                }
            }
        } finally {
            --this.blockingCount;
        }

    }

    protected void bq() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void executeTask(R r0) {
        try {
            r0.run();
        } catch (Exception exception) {
            IAsyncTaskHandler.LOGGER.fatal("Error executing task on {}", this.bn(), exception);
        }

    }

    @Override
    public List<MetricSampler> bk() {
        return ImmutableList.of(MetricSampler.a(this.name + "-pending-tasks", MetricCategory.EVENT_LOOPS, this::bm));
    }
}

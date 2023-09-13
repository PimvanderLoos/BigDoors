package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public abstract class IAsyncTaskHandler<R extends Runnable> implements ProfilerMeasured, Mailbox<R>, Executor {

    private final String name;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
    private int blockingCount;

    protected IAsyncTaskHandler(String s) {
        this.name = s;
        MetricsRegistry.INSTANCE.add(this);
    }

    protected abstract R wrapRunnable(Runnable runnable);

    protected abstract boolean shouldRun(R r0);

    public boolean isSameThread() {
        return Thread.currentThread() == this.getRunningThread();
    }

    protected abstract Thread getRunningThread();

    protected boolean scheduleExecutables() {
        return !this.isSameThread();
    }

    public int getPendingTasksCount() {
        return this.pendingRunnables.size();
    }

    @Override
    public String name() {
        return this.name;
    }

    public <V> CompletableFuture<V> submit(Supplier<V> supplier) {
        return this.scheduleExecutables() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable runnable) {
        if (this.scheduleExecutables()) {
            return this.submitAsync(runnable);
        } else {
            runnable.run();
            return CompletableFuture.completedFuture((Object) null);
        }
    }

    public void executeBlocking(Runnable runnable) {
        if (!this.isSameThread()) {
            this.submitAsync(runnable).join();
        } else {
            runnable.run();
        }

    }

    public void tell(R r0) {
        this.pendingRunnables.add(r0);
        LockSupport.unpark(this.getRunningThread());
    }

    public void execute(Runnable runnable) {
        if (this.scheduleExecutables()) {
            this.tell(this.wrapRunnable(runnable));
        } else {
            runnable.run();
        }

    }

    public void executeIfPossible(Runnable runnable) {
        this.execute(runnable);
    }

    protected void dropAllTasks() {
        this.pendingRunnables.clear();
    }

    protected void runAllTasks() {
        while (this.pollTask()) {
            ;
        }

    }

    public boolean pollTask() {
        R r0 = (Runnable) this.pendingRunnables.peek();

        if (r0 == null) {
            return false;
        } else if (this.blockingCount == 0 && !this.shouldRun(r0)) {
            return false;
        } else {
            this.doRunTask((Runnable) this.pendingRunnables.remove());
            return true;
        }
    }

    public void managedBlock(BooleanSupplier booleansupplier) {
        ++this.blockingCount;

        try {
            while (!booleansupplier.getAsBoolean()) {
                if (!this.pollTask()) {
                    this.waitForTasks();
                }
            }
        } finally {
            --this.blockingCount;
        }

    }

    protected void waitForTasks() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void doRunTask(R r0) {
        try {
            r0.run();
        } catch (Exception exception) {
            IAsyncTaskHandler.LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", this.name(), exception);
        }

    }

    @Override
    public List<MetricSampler> profiledMetrics() {
        return ImmutableList.of(MetricSampler.create(this.name + "-pending-tasks", MetricCategory.EVENT_LOOPS, this::getPendingTasksCount));
    }
}

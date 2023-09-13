package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SystemUtils;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.slf4j.Logger;

public class ThreadedMailbox<T> implements ProfilerMeasured, Mailbox<T>, AutoCloseable, Runnable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CLOSED_BIT = 1;
    private static final int SCHEDULED_BIT = 2;
    private final AtomicInteger status = new AtomicInteger(0);
    private final PairedQueue<? super T, ? extends Runnable> queue;
    private final Executor dispatcher;
    private final String name;

    public static ThreadedMailbox<Runnable> create(Executor executor, String s) {
        return new ThreadedMailbox<>(new PairedQueue.c<>(new ConcurrentLinkedQueue()), executor, s);
    }

    public ThreadedMailbox(PairedQueue<? super T, ? extends Runnable> pairedqueue, Executor executor, String s) {
        this.dispatcher = executor;
        this.queue = pairedqueue;
        this.name = s;
        MetricsRegistry.INSTANCE.add(this);
    }

    private boolean setAsScheduled() {
        int i;

        do {
            i = this.status.get();
            if ((i & 3) != 0) {
                return false;
            }
        } while (!this.status.compareAndSet(i, i | 2));

        return true;
    }

    private void setAsIdle() {
        int i;

        do {
            i = this.status.get();
        } while (!this.status.compareAndSet(i, i & -3));

    }

    private boolean canBeScheduled() {
        return (this.status.get() & 1) != 0 ? false : !this.queue.isEmpty();
    }

    @Override
    public void close() {
        int i;

        do {
            i = this.status.get();
        } while (!this.status.compareAndSet(i, i | 1));

    }

    private boolean shouldProcess() {
        return (this.status.get() & 2) != 0;
    }

    private boolean pollTask() {
        if (!this.shouldProcess()) {
            return false;
        } else {
            Runnable runnable = (Runnable) this.queue.pop();

            if (runnable == null) {
                return false;
            } else {
                SystemUtils.wrapThreadWithTaskName(this.name, runnable).run();
                return true;
            }
        }
    }

    public void run() {
        try {
            this.pollUntil((i) -> {
                return i == 0;
            });
        } finally {
            this.setAsIdle();
            this.registerForExecution();
        }

    }

    public void runAll() {
        try {
            this.pollUntil((i) -> {
                return true;
            });
        } finally {
            this.setAsIdle();
            this.registerForExecution();
        }

    }

    @Override
    public void tell(T t0) {
        this.queue.push(t0);
        this.registerForExecution();
    }

    private void registerForExecution() {
        if (this.canBeScheduled() && this.setAsScheduled()) {
            try {
                this.dispatcher.execute(this);
            } catch (RejectedExecutionException rejectedexecutionexception) {
                try {
                    this.dispatcher.execute(this);
                } catch (RejectedExecutionException rejectedexecutionexception1) {
                    ThreadedMailbox.LOGGER.error("Cound not schedule mailbox", rejectedexecutionexception1);
                }
            }
        }

    }

    private int pollUntil(Int2BooleanFunction int2booleanfunction) {
        int i;

        for (i = 0; int2booleanfunction.get(i) && this.pollTask(); ++i) {
            ;
        }

        return i;
    }

    public int size() {
        return this.queue.size();
    }

    public boolean hasWork() {
        return this.shouldProcess() && !this.queue.isEmpty();
    }

    public String toString() {
        return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<MetricSampler> profiledMetrics() {
        return ImmutableList.of(MetricSampler.create(this.name + "-queue-size", MetricCategory.MAIL_BOXES, this::size));
    }
}

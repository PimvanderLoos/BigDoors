package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedMailbox<T> implements ProfilerMeasured, Mailbox<T>, AutoCloseable, Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CLOSED_BIT = 1;
    private static final int SCHEDULED_BIT = 2;
    private final AtomicInteger status = new AtomicInteger(0);
    private final PairedQueue<? super T, ? extends Runnable> queue;
    private final Executor dispatcher;
    private final String name;

    public static ThreadedMailbox<Runnable> a(Executor executor, String s) {
        return new ThreadedMailbox<>(new PairedQueue.c<>(new ConcurrentLinkedQueue()), executor, s);
    }

    public ThreadedMailbox(PairedQueue<? super T, ? extends Runnable> pairedqueue, Executor executor, String s) {
        this.dispatcher = executor;
        this.queue = pairedqueue;
        this.name = s;
        MetricsRegistry.INSTANCE.a((ProfilerMeasured) this);
    }

    private boolean c() {
        int i;

        do {
            i = this.status.get();
            if ((i & 3) != 0) {
                return false;
            }
        } while (!this.status.compareAndSet(i, i | 2));

        return true;
    }

    private void d() {
        int i;

        do {
            i = this.status.get();
        } while (!this.status.compareAndSet(i, i & -3));

    }

    private boolean e() {
        return (this.status.get() & 1) != 0 ? false : !this.queue.b();
    }

    @Override
    public void close() {
        int i;

        do {
            i = this.status.get();
        } while (!this.status.compareAndSet(i, i | 1));

    }

    private boolean f() {
        return (this.status.get() & 2) != 0;
    }

    private boolean g() {
        if (!this.f()) {
            return false;
        } else {
            Runnable runnable = (Runnable) this.queue.a();

            if (runnable == null) {
                return false;
            } else {
                SystemUtils.a(this.name, runnable).run();
                return true;
            }
        }
    }

    public void run() {
        try {
            this.a((i) -> {
                return i == 0;
            });
        } finally {
            this.d();
            this.h();
        }

    }

    public void a() {
        try {
            this.a((i) -> {
                return true;
            });
        } finally {
            this.d();
            this.h();
        }

    }

    @Override
    public void a(T t0) {
        this.queue.a(t0);
        this.h();
    }

    private void h() {
        if (this.e() && this.c()) {
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

    private int a(Int2BooleanFunction int2booleanfunction) {
        int i;

        for (i = 0; int2booleanfunction.get(i) && this.g(); ++i) {
            ;
        }

        return i;
    }

    public int b() {
        return this.queue.c();
    }

    public String toString() {
        return this.name + " " + this.status.get() + " " + this.queue.b();
    }

    @Override
    public String bn() {
        return this.name;
    }

    @Override
    public List<MetricSampler> bk() {
        return ImmutableList.of(MetricSampler.a(this.name + "-queue-size", MetricCategory.MAIL_BOXES, this::b));
    }
}

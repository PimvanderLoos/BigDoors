package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Scheduler<K, T extends SchedulerTask<K, T>, R> {

    private static final Logger b = LogManager.getLogger();
    protected final ExecutorService a;
    private final ExecutorService c;
    private final AtomicInteger d = new AtomicInteger(1);
    private final List<CompletableFuture<R>> e = Lists.newArrayList();
    private CompletableFuture<R> f = CompletableFuture.completedFuture((Object) null);
    private CompletableFuture<R> g = CompletableFuture.completedFuture((Object) null);
    private final Supplier<Map<T, CompletableFuture<R>>> h;
    private final Supplier<Map<T, CompletableFuture<Void>>> i;
    private final T j;

    public Scheduler(String s, int i, T t0, Supplier<Map<T, CompletableFuture<R>>> supplier, Supplier<Map<T, CompletableFuture<Void>>> supplier1) {
        this.j = t0;
        this.h = supplier;
        this.i = supplier1;
        if (i == 0) {
            this.a = MoreExecutors.newDirectExecutorService();
        } else {
            this.a = Executors.newSingleThreadExecutor(new NamedIncrementingThreadFactory(s + "-Scheduler"));
        }

        if (i <= 1) {
            this.c = MoreExecutors.newDirectExecutorService();
        } else {
            this.c = new ForkJoinPool(i - 1, (forkjoinpool) -> {
                return new ForkJoinWorkerThread(forkjoinpool) {
                    {
                        this.setName(s + "-Worker-" + Scheduler.this.d.getAndIncrement());
                    }
                };
            }, (thread, throwable) -> {
                Scheduler.b.error(String.format("Caught exception in thread %s", thread), throwable);
            }, true);
        }

    }

    public CompletableFuture<R> a(K k0) {
        CompletableFuture<R> completablefuture = this.f;
        Supplier<CompletableFuture<R>> supplier = () -> {
            return this.b(k0).a(completablefuture, this.j);
        };
        CompletableFuture<CompletableFuture<R>> completablefuture1 = CompletableFuture.supplyAsync(supplier, this.a);
        CompletableFuture<R> completablefuture2 = completablefuture1.thenComposeAsync((completablefuture3) -> {
            return completablefuture3;
        }, this.c);

        this.e.add(completablefuture2);
        return completablefuture2;
    }

    public CompletableFuture<R> a() {
        CompletableFuture<R> completablefuture = (CompletableFuture) this.e.remove(this.e.size() - 1);
        CompletableFuture<R> completablefuture1 = CompletableFuture.allOf((CompletableFuture[]) this.e.toArray(new CompletableFuture[0])).thenCompose((ovoid) -> {
            return completablefuture;
        });

        this.g = completablefuture1;
        this.e.clear();
        this.f = completablefuture1;
        return completablefuture1;
    }

    protected Scheduler<K, T, R>.a b(K k0) {
        return this.a(k0, true);
    }

    @Nullable
    protected abstract Scheduler<K, T, R>.a a(K k0, boolean flag);

    public void b() throws InterruptedException {
        this.a.shutdown();
        this.a.awaitTermination(1L, TimeUnit.DAYS);
        this.c.shutdown();
        this.c.awaitTermination(1L, TimeUnit.DAYS);
    }

    protected abstract R a(K k0, T t0, Map<K, R> map);

    @Nullable
    public R b(K k0, boolean flag) {
        Scheduler<K, T, R>.a scheduler_a = this.a(k0, flag);

        return scheduler_a != null ? scheduler_a.a() : null;
    }

    public CompletableFuture<R> c() {
        CompletableFuture<R> completablefuture = this.g;

        return completablefuture.thenApply((object) -> {
            return object;
        });
    }

    protected abstract void b(K k0, Scheduler<K, T, R>.a scheduler_a);

    protected abstract Scheduler<K, T, R>.a a(K k0, Scheduler<K, T, R>.a scheduler_a);

    public final class a {

        private final Map<T, CompletableFuture<R>> b;
        private final K c;
        private final R d;

        public a(Object object, Object object1, SchedulerTask schedulertask) {
            this.b = (Map) Scheduler.this.h.get();
            this.c = object;

            for (this.d = object1; schedulertask != null; schedulertask = schedulertask.a()) {
                this.b.put(schedulertask, CompletableFuture.completedFuture(object1));
            }

        }

        public R a() {
            return this.d;
        }

        private CompletableFuture<R> a(CompletableFuture<R> completablefuture, T t0) {
            Map<K, CompletableFuture<R>> map = new ConcurrentHashMap();

            return (CompletableFuture) this.b.computeIfAbsent(t0, (schedulertask) -> {
                if (t0.a() == null) {
                    return CompletableFuture.completedFuture(this.d);
                } else {
                    t0.a(this.c, (object, schedulertask1) -> {
                        CompletableFuture completablefuture1 = (CompletableFuture) map.put(object, Scheduler.this.a(object, Scheduler.this.b(object)).a(completablefuture, schedulertask1));
                    });
                    CompletableFuture<?>[] acompletablefuture = (CompletableFuture[]) Streams.concat(new Stream[] { Stream.of(completablefuture), map.values().stream()}).toArray((i) -> {
                        return new CompletableFuture[i];
                    });
                    CompletableFuture<R> completablefuture1 = CompletableFuture.allOf(acompletablefuture).thenApplyAsync((ovoid) -> {
                        return Scheduler.this.a(this.c, t0, Maps.transformValues(map, (completablefuture2) -> {
                            try {
                                return completablefuture2.get();
                            } catch (ExecutionException | InterruptedException interruptedexception) {
                                throw new RuntimeException(interruptedexception);
                            }
                        }));
                    }, Scheduler.this.c).thenApplyAsync((object) -> {
                        Iterator iterator = map.keySet().iterator();

                        while (iterator.hasNext()) {
                            K k0 = iterator.next();

                            Scheduler.this.b(k0, Scheduler.this.b(k0));
                        }

                        return object;
                    }, Scheduler.this.a);

                    this.b.put(t0, completablefuture1);
                    return completablefuture1;
                }
            });
        }
    }
}

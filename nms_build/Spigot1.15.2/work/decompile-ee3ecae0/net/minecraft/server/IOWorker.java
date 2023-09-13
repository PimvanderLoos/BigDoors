package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Thread b;
    private final AtomicBoolean c = new AtomicBoolean();
    private final Queue<Runnable> d = Queues.newConcurrentLinkedQueue();
    private final RegionFileCache e;
    private final Map<ChunkCoordIntPair, IOWorker.a> f = Maps.newLinkedHashMap();
    private boolean g = true;
    private CompletableFuture<Void> h = new CompletableFuture();

    IOWorker(RegionFileCache regionfilecache, String s) {
        this.e = regionfilecache;
        this.b = new Thread(this::d);
        this.b.setName(s + " IO worker");
        this.b.start();
    }

    public CompletableFuture<Void> a(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        return this.a((completablefuture) -> {
            return () -> {
                IOWorker.a ioworker_a = (IOWorker.a) this.f.computeIfAbsent(chunkcoordintpair, (chunkcoordintpair1) -> {
                    return new IOWorker.a();
                });

                ioworker_a.a = nbttagcompound;
                ioworker_a.b.whenComplete((ovoid, throwable) -> {
                    if (throwable != null) {
                        completablefuture.completeExceptionally(throwable);
                    } else {
                        completablefuture.complete((Object) null);
                    }

                });
            };
        });
    }

    @Nullable
    public NBTTagCompound a(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        CompletableFuture completablefuture = this.a((completablefuture1) -> {
            return () -> {
                IOWorker.a ioworker_a = (IOWorker.a) this.f.get(chunkcoordintpair);

                if (ioworker_a != null) {
                    completablefuture1.complete(ioworker_a.a);
                } else {
                    try {
                        NBTTagCompound nbttagcompound = this.e.read(chunkcoordintpair);

                        completablefuture1.complete(nbttagcompound);
                    } catch (Exception exception) {
                        IOWorker.LOGGER.warn("Failed to read chunk {}", chunkcoordintpair, exception);
                        completablefuture1.completeExceptionally(exception);
                    }
                }

            };
        });

        try {
            return (NBTTagCompound) completablefuture.join();
        } catch (CompletionException completionexception) {
            if (completionexception.getCause() instanceof IOException) {
                throw (IOException) completionexception.getCause();
            } else {
                throw completionexception;
            }
        }
    }

    private CompletableFuture<Void> b() {
        return this.a((completablefuture) -> {
            return () -> {
                this.g = false;
                this.h = completablefuture;
            };
        });
    }

    public CompletableFuture<Void> a() {
        return this.a((completablefuture) -> {
            return () -> {
                CompletableFuture<?> completablefuture1 = CompletableFuture.allOf((CompletableFuture[]) this.f.values().stream().map((ioworker_a) -> {
                    return ioworker_a.b;
                }).toArray((i) -> {
                    return new CompletableFuture[i];
                }));

                completablefuture1.whenComplete((object, throwable) -> {
                    completablefuture.complete((Object) null);
                });
            };
        });
    }

    private <T> CompletableFuture<T> a(Function<CompletableFuture<T>, Runnable> function) {
        CompletableFuture<T> completablefuture = new CompletableFuture();

        this.d.add(function.apply(completablefuture));
        LockSupport.unpark(this.b);
        return completablefuture;
    }

    private void c() {
        LockSupport.park("waiting for tasks");
    }

    private void d() {
        try {
            while (this.g) {
                boolean flag = this.h();
                boolean flag1 = this.e();

                if (!flag && !flag1) {
                    this.c();
                }
            }

            this.h();
            this.f();
        } finally {
            this.g();
        }

    }

    private boolean e() {
        Iterator<Entry<ChunkCoordIntPair, IOWorker.a>> iterator = this.f.entrySet().iterator();

        if (!iterator.hasNext()) {
            return false;
        } else {
            Entry<ChunkCoordIntPair, IOWorker.a> entry = (Entry) iterator.next();

            iterator.remove();
            this.a((ChunkCoordIntPair) entry.getKey(), (IOWorker.a) entry.getValue());
            return true;
        }
    }

    private void f() {
        this.f.forEach(this::a);
        this.f.clear();
    }

    private void a(ChunkCoordIntPair chunkcoordintpair, IOWorker.a ioworker_a) {
        try {
            this.e.write(chunkcoordintpair, ioworker_a.a);
            ioworker_a.b.complete((Object) null);
        } catch (Exception exception) {
            IOWorker.LOGGER.error("Failed to store chunk {}", chunkcoordintpair, exception);
            ioworker_a.b.completeExceptionally(exception);
        }

    }

    private void g() {
        try {
            this.e.close();
            this.h.complete((Object) null);
        } catch (Exception exception) {
            IOWorker.LOGGER.error("Failed to close storage", exception);
            this.h.completeExceptionally(exception);
        }

    }

    private boolean h() {
        boolean flag = false;

        Runnable runnable;

        while ((runnable = (Runnable) this.d.poll()) != null) {
            flag = true;
            runnable.run();
        }

        return flag;
    }

    public void close() throws IOException {
        if (this.c.compareAndSet(false, true)) {
            try {
                this.b().join();
            } catch (CompletionException completionexception) {
                if (completionexception.getCause() instanceof IOException) {
                    throw (IOException) completionexception.getCause();
                } else {
                    throw completionexception;
                }
            }
        }
    }

    static class a {

        private NBTTagCompound a;
        private final CompletableFuture<Void> b;

        private a() {
            this.b = new CompletableFuture();
        }
    }
}

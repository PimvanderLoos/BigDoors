package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.PairedQueue;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final ThreadedMailbox<PairedQueue.b> mailbox;
    private final RegionFileCache storage;
    private final Map<ChunkCoordIntPair, IOWorker.a> pendingWrites = Maps.newLinkedHashMap();

    protected IOWorker(File file, boolean flag, String s) {
        this.storage = new RegionFileCache(file, flag);
        this.mailbox = new ThreadedMailbox<>(new PairedQueue.a(IOWorker.Priority.values().length), SystemUtils.g(), "IOWorker-" + s);
    }

    public CompletableFuture<Void> a(ChunkCoordIntPair chunkcoordintpair, @Nullable NBTTagCompound nbttagcompound) {
        return this.a(() -> {
            IOWorker.a ioworker_a = (IOWorker.a) this.pendingWrites.computeIfAbsent(chunkcoordintpair, (chunkcoordintpair1) -> {
                return new IOWorker.a(nbttagcompound);
            });

            ioworker_a.data = nbttagcompound;
            return Either.left(ioworker_a.result);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public NBTTagCompound a(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        CompletableFuture completablefuture = this.b(chunkcoordintpair);

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

    protected CompletableFuture<NBTTagCompound> b(ChunkCoordIntPair chunkcoordintpair) {
        return this.a(() -> {
            IOWorker.a ioworker_a = (IOWorker.a) this.pendingWrites.get(chunkcoordintpair);

            if (ioworker_a != null) {
                return Either.left(ioworker_a.data);
            } else {
                try {
                    NBTTagCompound nbttagcompound = this.storage.read(chunkcoordintpair);

                    return Either.left(nbttagcompound);
                } catch (Exception exception) {
                    IOWorker.LOGGER.warn("Failed to read chunk {}", chunkcoordintpair, exception);
                    return Either.right(exception);
                }
            }
        });
    }

    public CompletableFuture<Void> a(boolean flag) {
        CompletableFuture<Void> completablefuture = this.a(() -> {
            return Either.left(CompletableFuture.allOf((CompletableFuture[]) this.pendingWrites.values().stream().map((ioworker_a) -> {
                return ioworker_a.result;
            }).toArray((i) -> {
                return new CompletableFuture[i];
            })));
        }).thenCompose(Function.identity());

        return flag ? completablefuture.thenCompose((ovoid) -> {
            return this.a(() -> {
                try {
                    this.storage.a();
                    return Either.left((Object) null);
                } catch (Exception exception) {
                    IOWorker.LOGGER.warn("Failed to synchronize chunks", exception);
                    return Either.right(exception);
                }
            });
        }) : completablefuture.thenCompose((ovoid) -> {
            return this.a(() -> {
                return Either.left((Object) null);
            });
        });
    }

    private <T> CompletableFuture<T> a(Supplier<Either<T, Exception>> supplier) {
        return this.mailbox.c((mailbox) -> {
            return new PairedQueue.b(IOWorker.Priority.FOREGROUND.ordinal(), () -> {
                if (!this.shutdownRequested.get()) {
                    mailbox.a((Either) supplier.get());
                }

                this.b();
            });
        });
    }

    private void a() {
        if (!this.pendingWrites.isEmpty()) {
            Iterator<Entry<ChunkCoordIntPair, IOWorker.a>> iterator = this.pendingWrites.entrySet().iterator();
            Entry<ChunkCoordIntPair, IOWorker.a> entry = (Entry) iterator.next();

            iterator.remove();
            this.a((ChunkCoordIntPair) entry.getKey(), (IOWorker.a) entry.getValue());
            this.b();
        }
    }

    private void b() {
        this.mailbox.a((Object) (new PairedQueue.b(IOWorker.Priority.BACKGROUND.ordinal(), this::a)));
    }

    private void a(ChunkCoordIntPair chunkcoordintpair, IOWorker.a ioworker_a) {
        try {
            this.storage.write(chunkcoordintpair, ioworker_a.data);
            ioworker_a.result.complete((Object) null);
        } catch (Exception exception) {
            IOWorker.LOGGER.error("Failed to store chunk {}", chunkcoordintpair, exception);
            ioworker_a.result.completeExceptionally(exception);
        }

    }

    public void close() throws IOException {
        if (this.shutdownRequested.compareAndSet(false, true)) {
            this.mailbox.b((mailbox) -> {
                return new PairedQueue.b(IOWorker.Priority.SHUTDOWN.ordinal(), () -> {
                    mailbox.a(Unit.INSTANCE);
                });
            }).join();
            this.mailbox.close();

            try {
                this.storage.close();
            } catch (Exception exception) {
                IOWorker.LOGGER.error("Failed to close storage", exception);
            }

        }
    }

    private static enum Priority {

        FOREGROUND, BACKGROUND, SHUTDOWN;

        private Priority() {}
    }

    private static class a {

        @Nullable
        NBTTagCompound data;
        final CompletableFuture<Void> result = new CompletableFuture();

        public a(@Nullable NBTTagCompound nbttagcompound) {
            this.data = nbttagcompound;
        }
    }
}

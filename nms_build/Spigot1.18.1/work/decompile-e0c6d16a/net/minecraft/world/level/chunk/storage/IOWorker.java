package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.IOException;
import java.nio.file.Path;
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
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.PairedQueue;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements ChunkScanAccess, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final ThreadedMailbox<PairedQueue.b> mailbox;
    private final RegionFileCache storage;
    private final Map<ChunkCoordIntPair, IOWorker.a> pendingWrites = Maps.newLinkedHashMap();

    protected IOWorker(Path path, boolean flag, String s) {
        this.storage = new RegionFileCache(path, flag);
        this.mailbox = new ThreadedMailbox<>(new PairedQueue.a(IOWorker.Priority.values().length), SystemUtils.ioPool(), "IOWorker-" + s);
    }

    public CompletableFuture<Void> store(ChunkCoordIntPair chunkcoordintpair, @Nullable NBTTagCompound nbttagcompound) {
        return this.submitTask(() -> {
            IOWorker.a ioworker_a = (IOWorker.a) this.pendingWrites.computeIfAbsent(chunkcoordintpair, (chunkcoordintpair1) -> {
                return new IOWorker.a(nbttagcompound);
            });

            ioworker_a.data = nbttagcompound;
            return Either.left(ioworker_a.result);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public NBTTagCompound load(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        CompletableFuture completablefuture = this.loadAsync(chunkcoordintpair);

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

    protected CompletableFuture<NBTTagCompound> loadAsync(ChunkCoordIntPair chunkcoordintpair) {
        return this.submitTask(() -> {
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

    public CompletableFuture<Void> synchronize(boolean flag) {
        CompletableFuture<Void> completablefuture = this.submitTask(() -> {
            return Either.left(CompletableFuture.allOf((CompletableFuture[]) this.pendingWrites.values().stream().map((ioworker_a) -> {
                return ioworker_a.result;
            }).toArray((i) -> {
                return new CompletableFuture[i];
            })));
        }).thenCompose(Function.identity());

        return flag ? completablefuture.thenCompose((ovoid) -> {
            return this.submitTask(() -> {
                try {
                    this.storage.flush();
                    return Either.left((Object) null);
                } catch (Exception exception) {
                    IOWorker.LOGGER.warn("Failed to synchronize chunks", exception);
                    return Either.right(exception);
                }
            });
        }) : completablefuture.thenCompose((ovoid) -> {
            return this.submitTask(() -> {
                return Either.left((Object) null);
            });
        });
    }

    @Override
    public CompletableFuture<Void> scanChunk(ChunkCoordIntPair chunkcoordintpair, StreamTagVisitor streamtagvisitor) {
        return this.submitTask(() -> {
            try {
                IOWorker.a ioworker_a = (IOWorker.a) this.pendingWrites.get(chunkcoordintpair);

                if (ioworker_a != null) {
                    if (ioworker_a.data != null) {
                        ioworker_a.data.acceptAsRoot(streamtagvisitor);
                    }
                } else {
                    this.storage.scanChunk(chunkcoordintpair, streamtagvisitor);
                }

                return Either.left((Object) null);
            } catch (Exception exception) {
                IOWorker.LOGGER.warn("Failed to bulk scan chunk {}", chunkcoordintpair, exception);
                return Either.right(exception);
            }
        });
    }

    private <T> CompletableFuture<T> submitTask(Supplier<Either<T, Exception>> supplier) {
        return this.mailbox.askEither((mailbox) -> {
            return new PairedQueue.b(IOWorker.Priority.FOREGROUND.ordinal(), () -> {
                if (!this.shutdownRequested.get()) {
                    mailbox.tell((Either) supplier.get());
                }

                this.tellStorePending();
            });
        });
    }

    private void storePendingChunk() {
        if (!this.pendingWrites.isEmpty()) {
            Iterator<Entry<ChunkCoordIntPair, IOWorker.a>> iterator = this.pendingWrites.entrySet().iterator();
            Entry<ChunkCoordIntPair, IOWorker.a> entry = (Entry) iterator.next();

            iterator.remove();
            this.runStore((ChunkCoordIntPair) entry.getKey(), (IOWorker.a) entry.getValue());
            this.tellStorePending();
        }
    }

    private void tellStorePending() {
        this.mailbox.tell(new PairedQueue.b(IOWorker.Priority.BACKGROUND.ordinal(), this::storePendingChunk));
    }

    private void runStore(ChunkCoordIntPair chunkcoordintpair, IOWorker.a ioworker_a) {
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
            this.mailbox.ask((mailbox) -> {
                return new PairedQueue.b(IOWorker.Priority.SHUTDOWN.ordinal(), () -> {
                    mailbox.tell(Unit.INSTANCE);
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

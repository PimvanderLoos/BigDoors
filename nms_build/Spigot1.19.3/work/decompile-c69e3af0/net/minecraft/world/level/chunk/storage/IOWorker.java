package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.PairedQueue;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.slf4j.Logger;

public class IOWorker implements ChunkScanAccess, AutoCloseable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean();
    private final ThreadedMailbox<PairedQueue.b> mailbox;
    private final RegionFileCache storage;
    private final Map<ChunkCoordIntPair, IOWorker.a> pendingWrites = Maps.newLinkedHashMap();
    private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap();
    private static final int REGION_CACHE_SIZE = 1024;

    protected IOWorker(Path path, boolean flag, String s) {
        this.storage = new RegionFileCache(path, flag);
        this.mailbox = new ThreadedMailbox<>(new PairedQueue.a(IOWorker.Priority.values().length), SystemUtils.ioPool(), "IOWorker-" + s);
    }

    public boolean isOldChunkAround(ChunkCoordIntPair chunkcoordintpair, int i) {
        ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(chunkcoordintpair.x - i, chunkcoordintpair.z - i);
        ChunkCoordIntPair chunkcoordintpair2 = new ChunkCoordIntPair(chunkcoordintpair.x + i, chunkcoordintpair.z + i);

        for (int j = chunkcoordintpair1.getRegionX(); j <= chunkcoordintpair2.getRegionX(); ++j) {
            for (int k = chunkcoordintpair1.getRegionZ(); k <= chunkcoordintpair2.getRegionZ(); ++k) {
                BitSet bitset = (BitSet) this.getOrCreateOldDataForRegion(j, k).join();

                if (!bitset.isEmpty()) {
                    ChunkCoordIntPair chunkcoordintpair3 = ChunkCoordIntPair.minFromRegion(j, k);
                    int l = Math.max(chunkcoordintpair1.x - chunkcoordintpair3.x, 0);
                    int i1 = Math.max(chunkcoordintpair1.z - chunkcoordintpair3.z, 0);
                    int j1 = Math.min(chunkcoordintpair2.x - chunkcoordintpair3.x, 31);
                    int k1 = Math.min(chunkcoordintpair2.z - chunkcoordintpair3.z, 31);

                    for (int l1 = l; l1 <= j1; ++l1) {
                        for (int i2 = i1; i2 <= k1; ++i2) {
                            int j2 = i2 * 32 + l1;

                            if (bitset.get(j2)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int i, int j) {
        long k = ChunkCoordIntPair.asLong(i, j);
        Long2ObjectLinkedOpenHashMap long2objectlinkedopenhashmap = this.regionCacheForBlender;

        synchronized (this.regionCacheForBlender) {
            CompletableFuture<BitSet> completablefuture = (CompletableFuture) this.regionCacheForBlender.getAndMoveToFirst(k);

            if (completablefuture == null) {
                completablefuture = this.createOldDataForRegion(i, j);
                this.regionCacheForBlender.putAndMoveToFirst(k, completablefuture);
                if (this.regionCacheForBlender.size() > 1024) {
                    this.regionCacheForBlender.removeLast();
                }
            }

            return completablefuture;
        }
    }

    private CompletableFuture<BitSet> createOldDataForRegion(int i, int j) {
        return CompletableFuture.supplyAsync(() -> {
            ChunkCoordIntPair chunkcoordintpair = ChunkCoordIntPair.minFromRegion(i, j);
            ChunkCoordIntPair chunkcoordintpair1 = ChunkCoordIntPair.maxFromRegion(i, j);
            BitSet bitset = new BitSet();

            ChunkCoordIntPair.rangeClosed(chunkcoordintpair, chunkcoordintpair1).forEach((chunkcoordintpair2) -> {
                CollectFields collectfields = new CollectFields(new FieldSelector[]{new FieldSelector(NBTTagInt.TYPE, "DataVersion"), new FieldSelector(NBTTagCompound.TYPE, "blending_data")});

                try {
                    this.scanChunk(chunkcoordintpair2, collectfields).join();
                } catch (Exception exception) {
                    IOWorker.LOGGER.warn("Failed to scan chunk {}", chunkcoordintpair2, exception);
                    return;
                }

                NBTBase nbtbase = collectfields.getResult();

                if (nbtbase instanceof NBTTagCompound) {
                    NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                    if (this.isOldChunk(nbttagcompound)) {
                        int k = chunkcoordintpair2.getRegionLocalZ() * 32 + chunkcoordintpair2.getRegionLocalX();

                        bitset.set(k);
                    }
                }

            });
            return bitset;
        }, SystemUtils.backgroundExecutor());
    }

    private boolean isOldChunk(NBTTagCompound nbttagcompound) {
        return nbttagcompound.contains("DataVersion", 99) && nbttagcompound.getInt("DataVersion") >= 3088 ? nbttagcompound.contains("blending_data", 10) : true;
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

    public CompletableFuture<Optional<NBTTagCompound>> loadAsync(ChunkCoordIntPair chunkcoordintpair) {
        return this.submitTask(() -> {
            IOWorker.a ioworker_a = (IOWorker.a) this.pendingWrites.get(chunkcoordintpair);

            if (ioworker_a != null) {
                return Either.left(Optional.ofNullable(ioworker_a.data));
            } else {
                try {
                    NBTTagCompound nbttagcompound = this.storage.read(chunkcoordintpair);

                    return Either.left(Optional.ofNullable(nbttagcompound));
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

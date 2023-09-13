package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkCoordIntPair;

public class ChunkTaskQueue<T> {

    public static final int PRIORITY_LEVEL_COUNT = PlayerChunkMap.MAX_CHUNK_DISTANCE + 2;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> taskQueue;
    private volatile int firstQueue;
    private final String name;
    private final LongSet acquired;
    private final int maxTasks;

    public ChunkTaskQueue(String s, int i) {
        this.taskQueue = (List) IntStream.range(0, ChunkTaskQueue.PRIORITY_LEVEL_COUNT).mapToObj((j) -> {
            return new Long2ObjectLinkedOpenHashMap();
        }).collect(Collectors.toList());
        this.firstQueue = ChunkTaskQueue.PRIORITY_LEVEL_COUNT;
        this.acquired = new LongOpenHashSet();
        this.name = s;
        this.maxTasks = i;
    }

    protected void a(int i, ChunkCoordIntPair chunkcoordintpair, int j) {
        if (i < ChunkTaskQueue.PRIORITY_LEVEL_COUNT) {
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = (Long2ObjectLinkedOpenHashMap) this.taskQueue.get(i);
            List<Optional<T>> list = (List) long2objectlinkedopenhashmap.remove(chunkcoordintpair.pair());

            if (i == this.firstQueue) {
                while (this.firstQueue < ChunkTaskQueue.PRIORITY_LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap) this.taskQueue.get(this.firstQueue)).isEmpty()) {
                    ++this.firstQueue;
                }
            }

            if (list != null && !list.isEmpty()) {
                ((List) ((Long2ObjectLinkedOpenHashMap) this.taskQueue.get(j)).computeIfAbsent(chunkcoordintpair.pair(), (k) -> {
                    return Lists.newArrayList();
                })).addAll(list);
                this.firstQueue = Math.min(this.firstQueue, j);
            }

        }
    }

    protected void a(Optional<T> optional, long i, int j) {
        ((List) ((Long2ObjectLinkedOpenHashMap) this.taskQueue.get(j)).computeIfAbsent(i, (k) -> {
            return Lists.newArrayList();
        })).add(optional);
        this.firstQueue = Math.min(this.firstQueue, j);
    }

    protected void a(long i, boolean flag) {
        Iterator iterator = this.taskQueue.iterator();

        while (iterator.hasNext()) {
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = (Long2ObjectLinkedOpenHashMap) iterator.next();
            List<Optional<T>> list = (List) long2objectlinkedopenhashmap.get(i);

            if (list != null) {
                if (flag) {
                    list.clear();
                } else {
                    list.removeIf((optional) -> {
                        return !optional.isPresent();
                    });
                }

                if (list.isEmpty()) {
                    long2objectlinkedopenhashmap.remove(i);
                }
            }
        }

        while (this.firstQueue < ChunkTaskQueue.PRIORITY_LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap) this.taskQueue.get(this.firstQueue)).isEmpty()) {
            ++this.firstQueue;
        }

        this.acquired.remove(i);
    }

    private Runnable a(long i) {
        return () -> {
            this.acquired.add(i);
        };
    }

    @Nullable
    public Stream<Either<T, Runnable>> a() {
        if (this.acquired.size() >= this.maxTasks) {
            return null;
        } else if (this.firstQueue >= ChunkTaskQueue.PRIORITY_LEVEL_COUNT) {
            return null;
        } else {
            int i = this.firstQueue;
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = (Long2ObjectLinkedOpenHashMap) this.taskQueue.get(i);
            long j = long2objectlinkedopenhashmap.firstLongKey();

            List list;

            for (list = (List) long2objectlinkedopenhashmap.removeFirst(); this.firstQueue < ChunkTaskQueue.PRIORITY_LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap) this.taskQueue.get(this.firstQueue)).isEmpty(); ++this.firstQueue) {
                ;
            }

            return list.stream().map((optional) -> {
                return (Either) optional.map(Either::left).orElseGet(() -> {
                    return Either.right(this.a(j));
                });
            });
        }
    }

    public String toString() {
        return this.name + " " + this.firstQueue + "...";
    }

    @VisibleForTesting
    LongSet b() {
        return new LongOpenHashSet(this.acquired);
    }
}

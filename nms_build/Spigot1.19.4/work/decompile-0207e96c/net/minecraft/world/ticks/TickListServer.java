package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class TickListServer<T> implements LevelTickAccess<T> {

    private static final Comparator<LevelChunkTicks<?>> CONTAINER_DRAIN_ORDER = (levelchunkticks, levelchunkticks1) -> {
        return NextTickListEntry.INTRA_TICK_DRAIN_ORDER.compare(levelchunkticks.peek(), levelchunkticks1.peek());
    };
    private final LongPredicate tickCheck;
    private final Supplier<GameProfilerFiller> profiler;
    private final Long2ObjectMap<LevelChunkTicks<T>> allContainers = new Long2ObjectOpenHashMap();
    private final Long2LongMap nextTickForContainer = (Long2LongMap) SystemUtils.make(new Long2LongOpenHashMap(), (long2longopenhashmap) -> {
        long2longopenhashmap.defaultReturnValue(Long.MAX_VALUE);
    });
    private final Queue<LevelChunkTicks<T>> containersToTick;
    private final Queue<NextTickListEntry<T>> toRunThisTick;
    private final List<NextTickListEntry<T>> alreadyRunThisTick;
    private final Set<NextTickListEntry<?>> toRunThisTickSet;
    private final BiConsumer<LevelChunkTicks<T>, NextTickListEntry<T>> chunkScheduleUpdater;

    public TickListServer(LongPredicate longpredicate, Supplier<GameProfilerFiller> supplier) {
        this.containersToTick = new PriorityQueue(TickListServer.CONTAINER_DRAIN_ORDER);
        this.toRunThisTick = new ArrayDeque();
        this.alreadyRunThisTick = new ArrayList();
        this.toRunThisTickSet = new ObjectOpenCustomHashSet(NextTickListEntry.UNIQUE_TICK_HASH);
        this.chunkScheduleUpdater = (levelchunkticks, nextticklistentry) -> {
            if (nextticklistentry.equals(levelchunkticks.peek())) {
                this.updateContainerScheduling(nextticklistentry);
            }

        };
        this.tickCheck = longpredicate;
        this.profiler = supplier;
    }

    public void addContainer(ChunkCoordIntPair chunkcoordintpair, LevelChunkTicks<T> levelchunkticks) {
        long i = chunkcoordintpair.toLong();

        this.allContainers.put(i, levelchunkticks);
        NextTickListEntry<T> nextticklistentry = levelchunkticks.peek();

        if (nextticklistentry != null) {
            this.nextTickForContainer.put(i, nextticklistentry.triggerTick());
        }

        levelchunkticks.setOnTickAdded(this.chunkScheduleUpdater);
    }

    public void removeContainer(ChunkCoordIntPair chunkcoordintpair) {
        long i = chunkcoordintpair.toLong();
        LevelChunkTicks<T> levelchunkticks = (LevelChunkTicks) this.allContainers.remove(i);

        this.nextTickForContainer.remove(i);
        if (levelchunkticks != null) {
            levelchunkticks.setOnTickAdded((BiConsumer) null);
        }

    }

    @Override
    public void schedule(NextTickListEntry<T> nextticklistentry) {
        long i = ChunkCoordIntPair.asLong(nextticklistentry.pos());
        LevelChunkTicks<T> levelchunkticks = (LevelChunkTicks) this.allContainers.get(i);

        if (levelchunkticks == null) {
            SystemUtils.pauseInIde(new IllegalStateException("Trying to schedule tick in not loaded position " + nextticklistentry.pos()));
        } else {
            levelchunkticks.schedule(nextticklistentry);
        }
    }

    public void tick(long i, int j, BiConsumer<BlockPosition, T> biconsumer) {
        GameProfilerFiller gameprofilerfiller = (GameProfilerFiller) this.profiler.get();

        gameprofilerfiller.push("collect");
        this.collectTicks(i, j, gameprofilerfiller);
        gameprofilerfiller.popPush("run");
        gameprofilerfiller.incrementCounter("ticksToRun", this.toRunThisTick.size());
        this.runCollectedTicks(biconsumer);
        gameprofilerfiller.popPush("cleanup");
        this.cleanupAfterTick();
        gameprofilerfiller.pop();
    }

    private void collectTicks(long i, int j, GameProfilerFiller gameprofilerfiller) {
        this.sortContainersToTick(i);
        gameprofilerfiller.incrementCounter("containersToTick", this.containersToTick.size());
        this.drainContainers(i, j);
        this.rescheduleLeftoverContainers();
    }

    private void sortContainersToTick(long i) {
        ObjectIterator objectiterator = Long2LongMaps.fastIterator(this.nextTickForContainer);

        while (objectiterator.hasNext()) {
            Entry entry = (Entry) objectiterator.next();
            long j = entry.getLongKey();
            long k = entry.getLongValue();

            if (k <= i) {
                LevelChunkTicks<T> levelchunkticks = (LevelChunkTicks) this.allContainers.get(j);

                if (levelchunkticks == null) {
                    objectiterator.remove();
                } else {
                    NextTickListEntry<T> nextticklistentry = levelchunkticks.peek();

                    if (nextticklistentry == null) {
                        objectiterator.remove();
                    } else if (nextticklistentry.triggerTick() > i) {
                        entry.setValue(nextticklistentry.triggerTick());
                    } else if (this.tickCheck.test(j)) {
                        objectiterator.remove();
                        this.containersToTick.add(levelchunkticks);
                    }
                }
            }
        }

    }

    private void drainContainers(long i, int j) {
        LevelChunkTicks levelchunkticks;

        while (this.canScheduleMoreTicks(j) && (levelchunkticks = (LevelChunkTicks) this.containersToTick.poll()) != null) {
            NextTickListEntry<T> nextticklistentry = levelchunkticks.poll();

            this.scheduleForThisTick(nextticklistentry);
            this.drainFromCurrentContainer(this.containersToTick, levelchunkticks, i, j);
            NextTickListEntry<T> nextticklistentry1 = levelchunkticks.peek();

            if (nextticklistentry1 != null) {
                if (nextticklistentry1.triggerTick() <= i && this.canScheduleMoreTicks(j)) {
                    this.containersToTick.add(levelchunkticks);
                } else {
                    this.updateContainerScheduling(nextticklistentry1);
                }
            }
        }

    }

    private void rescheduleLeftoverContainers() {
        Iterator iterator = this.containersToTick.iterator();

        while (iterator.hasNext()) {
            LevelChunkTicks<T> levelchunkticks = (LevelChunkTicks) iterator.next();

            this.updateContainerScheduling(levelchunkticks.peek());
        }

    }

    private void updateContainerScheduling(NextTickListEntry<T> nextticklistentry) {
        this.nextTickForContainer.put(ChunkCoordIntPair.asLong(nextticklistentry.pos()), nextticklistentry.triggerTick());
    }

    private void drainFromCurrentContainer(Queue<LevelChunkTicks<T>> queue, LevelChunkTicks<T> levelchunkticks, long i, int j) {
        if (this.canScheduleMoreTicks(j)) {
            LevelChunkTicks<T> levelchunkticks1 = (LevelChunkTicks) queue.peek();
            NextTickListEntry nextticklistentry = levelchunkticks1 != null ? levelchunkticks1.peek() : null;

            while (this.canScheduleMoreTicks(j)) {
                NextTickListEntry<T> nextticklistentry1 = levelchunkticks.peek();

                if (nextticklistentry1 == null || nextticklistentry1.triggerTick() > i || nextticklistentry != null && NextTickListEntry.INTRA_TICK_DRAIN_ORDER.compare(nextticklistentry1, nextticklistentry) > 0) {
                    break;
                }

                levelchunkticks.poll();
                this.scheduleForThisTick(nextticklistentry1);
            }

        }
    }

    private void scheduleForThisTick(NextTickListEntry<T> nextticklistentry) {
        this.toRunThisTick.add(nextticklistentry);
    }

    private boolean canScheduleMoreTicks(int i) {
        return this.toRunThisTick.size() < i;
    }

    private void runCollectedTicks(BiConsumer<BlockPosition, T> biconsumer) {
        while (!this.toRunThisTick.isEmpty()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) this.toRunThisTick.poll();

            if (!this.toRunThisTickSet.isEmpty()) {
                this.toRunThisTickSet.remove(nextticklistentry);
            }

            this.alreadyRunThisTick.add(nextticklistentry);
            biconsumer.accept(nextticklistentry.pos(), nextticklistentry.type());
        }

    }

    private void cleanupAfterTick() {
        this.toRunThisTick.clear();
        this.containersToTick.clear();
        this.alreadyRunThisTick.clear();
        this.toRunThisTickSet.clear();
    }

    @Override
    public boolean hasScheduledTick(BlockPosition blockposition, T t0) {
        LevelChunkTicks<T> levelchunkticks = (LevelChunkTicks) this.allContainers.get(ChunkCoordIntPair.asLong(blockposition));

        return levelchunkticks != null && levelchunkticks.hasScheduledTick(blockposition, t0);
    }

    @Override
    public boolean willTickThisTick(BlockPosition blockposition, T t0) {
        this.calculateTickSetIfNeeded();
        return this.toRunThisTickSet.contains(NextTickListEntry.probe(t0, blockposition));
    }

    private void calculateTickSetIfNeeded() {
        if (this.toRunThisTickSet.isEmpty() && !this.toRunThisTick.isEmpty()) {
            this.toRunThisTickSet.addAll(this.toRunThisTick);
        }

    }

    private void forContainersInArea(StructureBoundingBox structureboundingbox, TickListServer.a<T> ticklistserver_a) {
        int i = SectionPosition.posToSectionCoord((double) structureboundingbox.minX());
        int j = SectionPosition.posToSectionCoord((double) structureboundingbox.minZ());
        int k = SectionPosition.posToSectionCoord((double) structureboundingbox.maxX());
        int l = SectionPosition.posToSectionCoord((double) structureboundingbox.maxZ());

        for (int i1 = i; i1 <= k; ++i1) {
            for (int j1 = j; j1 <= l; ++j1) {
                long k1 = ChunkCoordIntPair.asLong(i1, j1);
                LevelChunkTicks<T> levelchunkticks = (LevelChunkTicks) this.allContainers.get(k1);

                if (levelchunkticks != null) {
                    ticklistserver_a.accept(k1, levelchunkticks);
                }
            }
        }

    }

    public void clearArea(StructureBoundingBox structureboundingbox) {
        Predicate<NextTickListEntry<T>> predicate = (nextticklistentry) -> {
            return structureboundingbox.isInside(nextticklistentry.pos());
        };

        this.forContainersInArea(structureboundingbox, (i, levelchunkticks) -> {
            NextTickListEntry<T> nextticklistentry = levelchunkticks.peek();

            levelchunkticks.removeIf(predicate);
            NextTickListEntry<T> nextticklistentry1 = levelchunkticks.peek();

            if (nextticklistentry1 != nextticklistentry) {
                if (nextticklistentry1 != null) {
                    this.updateContainerScheduling(nextticklistentry1);
                } else {
                    this.nextTickForContainer.remove(i);
                }
            }

        });
        this.alreadyRunThisTick.removeIf(predicate);
        this.toRunThisTick.removeIf(predicate);
    }

    public void copyArea(StructureBoundingBox structureboundingbox, BaseBlockPosition baseblockposition) {
        this.copyAreaFrom(this, structureboundingbox, baseblockposition);
    }

    public void copyAreaFrom(TickListServer<T> ticklistserver, StructureBoundingBox structureboundingbox, BaseBlockPosition baseblockposition) {
        List<NextTickListEntry<T>> list = new ArrayList();
        Predicate<NextTickListEntry<T>> predicate = (nextticklistentry) -> {
            return structureboundingbox.isInside(nextticklistentry.pos());
        };
        Stream stream = ticklistserver.alreadyRunThisTick.stream().filter(predicate);

        Objects.requireNonNull(list);
        stream.forEach(list::add);
        stream = ticklistserver.toRunThisTick.stream().filter(predicate);
        Objects.requireNonNull(list);
        stream.forEach(list::add);
        ticklistserver.forContainersInArea(structureboundingbox, (i, levelchunkticks) -> {
            Stream stream1 = levelchunkticks.getAll().filter(predicate);

            Objects.requireNonNull(list);
            stream1.forEach(list::add);
        });
        LongSummaryStatistics longsummarystatistics = list.stream().mapToLong(NextTickListEntry::subTickOrder).summaryStatistics();
        long i = longsummarystatistics.getMin();
        long j = longsummarystatistics.getMax();

        list.forEach((nextticklistentry) -> {
            this.schedule(new NextTickListEntry<>(nextticklistentry.type(), nextticklistentry.pos().offset(baseblockposition), nextticklistentry.triggerTick(), nextticklistentry.priority(), nextticklistentry.subTickOrder() - i + j + 1L));
        });
    }

    @Override
    public int count() {
        return this.allContainers.values().stream().mapToInt(TickList::count).sum();
    }

    @FunctionalInterface
    private interface a<T> {

        void accept(long i, LevelChunkTicks<T> levelchunkticks);
    }
}

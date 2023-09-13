package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.ArraySetSorted;
import net.minecraft.util.thread.Mailbox;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkMapDistance {

    static final Logger LOGGER = LogManager.getLogger();
    private static final int ENTITY_TICKING_RANGE = 2;
    static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    private static final int ENTITY_TICKING_LEVEL_THRESHOLD = 32;
    private static final int BLOCK_TICKING_LEVEL_THRESHOLD = 33;
    final Long2ObjectMap<ObjectSet<EntityPlayer>> playersPerChunk = new Long2ObjectOpenHashMap();
    public final Long2ObjectOpenHashMap<ArraySetSorted<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
    private final ChunkMapDistance.a ticketTracker = new ChunkMapDistance.a();
    private final ChunkMapDistance.b naturalSpawnChunkCounter = new ChunkMapDistance.b(8);
    private final TickingTracker tickingTicketsTracker = new TickingTracker();
    private final ChunkMapDistance.c playerTicketManager = new ChunkMapDistance.c(33);
    final Set<PlayerChunk> chunksToUpdateFutures = Sets.newHashSet();
    final ChunkTaskQueueSorter ticketThrottler;
    final Mailbox<ChunkTaskQueueSorter.a<Runnable>> ticketThrottlerInput;
    final Mailbox<ChunkTaskQueueSorter.b> ticketThrottlerReleaser;
    final LongSet ticketsToRelease = new LongOpenHashSet();
    final Executor mainThreadExecutor;
    private long ticketTickCounter;
    private int simulationDistance = 10;

    protected ChunkMapDistance(Executor executor, Executor executor1) {
        Objects.requireNonNull(executor1);
        Mailbox<Runnable> mailbox = Mailbox.of("player ticket throttler", executor1::execute);
        ChunkTaskQueueSorter chunktaskqueuesorter = new ChunkTaskQueueSorter(ImmutableList.of(mailbox), executor, 4);

        this.ticketThrottler = chunktaskqueuesorter;
        this.ticketThrottlerInput = chunktaskqueuesorter.getProcessor(mailbox, true);
        this.ticketThrottlerReleaser = chunktaskqueuesorter.getReleaseProcessor(mailbox);
        this.mainThreadExecutor = executor1;
    }

    protected void purgeStaleTickets() {
        ++this.ticketTickCounter;
        ObjectIterator objectiterator = this.tickets.long2ObjectEntrySet().fastIterator();

        while (objectiterator.hasNext()) {
            Entry<ArraySetSorted<Ticket<?>>> entry = (Entry) objectiterator.next();
            Iterator<Ticket<?>> iterator = ((ArraySetSorted) entry.getValue()).iterator();
            boolean flag = false;

            while (iterator.hasNext()) {
                Ticket<?> ticket = (Ticket) iterator.next();

                if (ticket.timedOut(this.ticketTickCounter)) {
                    iterator.remove();
                    flag = true;
                    this.tickingTicketsTracker.removeTicket(entry.getLongKey(), ticket);
                }
            }

            if (flag) {
                this.ticketTracker.update(entry.getLongKey(), getTicketLevelAt((ArraySetSorted) entry.getValue()), false);
            }

            if (((ArraySetSorted) entry.getValue()).isEmpty()) {
                objectiterator.remove();
            }
        }

    }

    private static int getTicketLevelAt(ArraySetSorted<Ticket<?>> arraysetsorted) {
        return !arraysetsorted.isEmpty() ? ((Ticket) arraysetsorted.first()).getTicketLevel() : PlayerChunkMap.MAX_CHUNK_DISTANCE + 1;
    }

    protected abstract boolean isChunkToRemove(long i);

    @Nullable
    protected abstract PlayerChunk getChunk(long i);

    @Nullable
    protected abstract PlayerChunk updateChunkScheduling(long i, int j, @Nullable PlayerChunk playerchunk, int k);

    public boolean runAllUpdates(PlayerChunkMap playerchunkmap) {
        this.naturalSpawnChunkCounter.runAllUpdates();
        this.tickingTicketsTracker.runAllUpdates();
        this.playerTicketManager.runAllUpdates();
        int i = Integer.MAX_VALUE - this.ticketTracker.runDistanceUpdates(Integer.MAX_VALUE);
        boolean flag = i != 0;

        if (flag) {
            ;
        }

        if (!this.chunksToUpdateFutures.isEmpty()) {
            this.chunksToUpdateFutures.forEach((playerchunk) -> {
                playerchunk.updateFutures(playerchunkmap, this.mainThreadExecutor);
            });
            this.chunksToUpdateFutures.clear();
            return true;
        } else {
            if (!this.ticketsToRelease.isEmpty()) {
                LongIterator longiterator = this.ticketsToRelease.iterator();

                while (longiterator.hasNext()) {
                    long j = longiterator.nextLong();

                    if (this.getTickets(j).stream().anyMatch((ticket) -> {
                        return ticket.getType() == TicketType.PLAYER;
                    })) {
                        PlayerChunk playerchunk = playerchunkmap.getUpdatingChunkIfPresent(j);

                        if (playerchunk == null) {
                            throw new IllegalStateException();
                        }

                        CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture = playerchunk.getEntityTickingChunkFuture();

                        completablefuture.thenAccept((either) -> {
                            this.mainThreadExecutor.execute(() -> {
                                this.ticketThrottlerReleaser.tell(ChunkTaskQueueSorter.release(() -> {
                                }, j, false));
                            });
                        });
                    }
                }

                this.ticketsToRelease.clear();
            }

            return flag;
        }
    }

    void addTicket(long i, Ticket<?> ticket) {
        ArraySetSorted<Ticket<?>> arraysetsorted = this.getTickets(i);
        int j = getTicketLevelAt(arraysetsorted);
        Ticket<?> ticket1 = (Ticket) arraysetsorted.addOrGet(ticket);

        ticket1.setCreatedTick(this.ticketTickCounter);
        if (ticket.getTicketLevel() < j) {
            this.ticketTracker.update(i, ticket.getTicketLevel(), true);
        }

    }

    void removeTicket(long i, Ticket<?> ticket) {
        ArraySetSorted<Ticket<?>> arraysetsorted = this.getTickets(i);

        if (arraysetsorted.remove(ticket)) {
            ;
        }

        if (arraysetsorted.isEmpty()) {
            this.tickets.remove(i);
        }

        this.ticketTracker.update(i, getTicketLevelAt(arraysetsorted), false);
    }

    public <T> void addTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.addTicket(chunkcoordintpair.toLong(), new Ticket<>(tickettype, i, t0));
    }

    public <T> void removeTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        Ticket<T> ticket = new Ticket<>(tickettype, i, t0);

        this.removeTicket(chunkcoordintpair.toLong(), ticket);
    }

    public <T> void addRegionTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        Ticket<T> ticket = new Ticket<>(tickettype, 33 - i, t0);
        long j = chunkcoordintpair.toLong();

        this.addTicket(j, ticket);
        this.tickingTicketsTracker.addTicket(j, ticket);
    }

    public <T> void removeRegionTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        Ticket<T> ticket = new Ticket<>(tickettype, 33 - i, t0);
        long j = chunkcoordintpair.toLong();

        this.removeTicket(j, ticket);
        this.tickingTicketsTracker.removeTicket(j, ticket);
    }

    private ArraySetSorted<Ticket<?>> getTickets(long i) {
        return (ArraySetSorted) this.tickets.computeIfAbsent(i, (j) -> {
            return ArraySetSorted.create(4);
        });
    }

    protected void updateChunkForced(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        Ticket<ChunkCoordIntPair> ticket = new Ticket<>(TicketType.FORCED, 31, chunkcoordintpair);
        long i = chunkcoordintpair.toLong();

        if (flag) {
            this.addTicket(i, ticket);
            this.tickingTicketsTracker.addTicket(i, ticket);
        } else {
            this.removeTicket(i, ticket);
            this.tickingTicketsTracker.removeTicket(i, ticket);
        }

    }

    public void addPlayer(SectionPosition sectionposition, EntityPlayer entityplayer) {
        ChunkCoordIntPair chunkcoordintpair = sectionposition.chunk();
        long i = chunkcoordintpair.toLong();

        ((ObjectSet) this.playersPerChunk.computeIfAbsent(i, (j) -> {
            return new ObjectOpenHashSet();
        })).add(entityplayer);
        this.naturalSpawnChunkCounter.update(i, 0, true);
        this.playerTicketManager.update(i, 0, true);
        this.tickingTicketsTracker.addTicket(TicketType.PLAYER, chunkcoordintpair, this.getPlayerTicketLevel(), chunkcoordintpair);
    }

    public void removePlayer(SectionPosition sectionposition, EntityPlayer entityplayer) {
        ChunkCoordIntPair chunkcoordintpair = sectionposition.chunk();
        long i = chunkcoordintpair.toLong();
        ObjectSet<EntityPlayer> objectset = (ObjectSet) this.playersPerChunk.get(i);

        objectset.remove(entityplayer);
        if (objectset.isEmpty()) {
            this.playersPerChunk.remove(i);
            this.naturalSpawnChunkCounter.update(i, Integer.MAX_VALUE, false);
            this.playerTicketManager.update(i, Integer.MAX_VALUE, false);
            this.tickingTicketsTracker.removeTicket(TicketType.PLAYER, chunkcoordintpair, this.getPlayerTicketLevel(), chunkcoordintpair);
        }

    }

    private int getPlayerTicketLevel() {
        return Math.max(0, 31 - this.simulationDistance);
    }

    public boolean inEntityTickingRange(long i) {
        return this.tickingTicketsTracker.getLevel(i) < 32;
    }

    public boolean inBlockTickingRange(long i) {
        return this.tickingTicketsTracker.getLevel(i) < 33;
    }

    protected String getTicketDebugString(long i) {
        ArraySetSorted<Ticket<?>> arraysetsorted = (ArraySetSorted) this.tickets.get(i);

        return arraysetsorted != null && !arraysetsorted.isEmpty() ? ((Ticket) arraysetsorted.first()).toString() : "no_ticket";
    }

    protected void updatePlayerTickets(int i) {
        this.playerTicketManager.updateViewDistance(i);
    }

    public void updateSimulationDistance(int i) {
        if (i != this.simulationDistance) {
            this.simulationDistance = i;
            this.tickingTicketsTracker.replacePlayerTicketsLevel(this.getPlayerTicketLevel());
        }

    }

    public int getNaturalSpawnChunkCount() {
        this.naturalSpawnChunkCounter.runAllUpdates();
        return this.naturalSpawnChunkCounter.chunks.size();
    }

    public boolean hasPlayersNearby(long i) {
        this.naturalSpawnChunkCounter.runAllUpdates();
        return this.naturalSpawnChunkCounter.chunks.containsKey(i);
    }

    public String getDebugStatus() {
        return this.ticketThrottler.getDebugStatus();
    }

    private void dumpTickets(String s) {
        try {
            FileOutputStream fileoutputstream = new FileOutputStream(new File(s));

            try {
                ObjectIterator objectiterator = this.tickets.long2ObjectEntrySet().iterator();

                while (objectiterator.hasNext()) {
                    Entry<ArraySetSorted<Ticket<?>>> entry = (Entry) objectiterator.next();
                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(entry.getLongKey());
                    Iterator iterator = ((ArraySetSorted) entry.getValue()).iterator();

                    while (iterator.hasNext()) {
                        Ticket<?> ticket = (Ticket) iterator.next();

                        fileoutputstream.write((chunkcoordintpair.x + "\t" + chunkcoordintpair.z + "\t" + ticket.getType() + "\t" + ticket.getTicketLevel() + "\t\n").getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (Throwable throwable) {
                try {
                    fileoutputstream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            fileoutputstream.close();
        } catch (IOException ioexception) {
            ChunkMapDistance.LOGGER.error(ioexception);
        }

    }

    @VisibleForTesting
    TickingTracker tickingTracker() {
        return this.tickingTicketsTracker;
    }

    private class a extends ChunkMap {

        public a() {
            super(PlayerChunkMap.MAX_CHUNK_DISTANCE + 2, 16, 256);
        }

        @Override
        protected int getLevelFromSource(long i) {
            ArraySetSorted<Ticket<?>> arraysetsorted = (ArraySetSorted) ChunkMapDistance.this.tickets.get(i);

            return arraysetsorted == null ? Integer.MAX_VALUE : (arraysetsorted.isEmpty() ? Integer.MAX_VALUE : ((Ticket) arraysetsorted.first()).getTicketLevel());
        }

        @Override
        protected int getLevel(long i) {
            if (!ChunkMapDistance.this.isChunkToRemove(i)) {
                PlayerChunk playerchunk = ChunkMapDistance.this.getChunk(i);

                if (playerchunk != null) {
                    return playerchunk.getTicketLevel();
                }
            }

            return PlayerChunkMap.MAX_CHUNK_DISTANCE + 1;
        }

        @Override
        protected void setLevel(long i, int j) {
            PlayerChunk playerchunk = ChunkMapDistance.this.getChunk(i);
            int k = playerchunk == null ? PlayerChunkMap.MAX_CHUNK_DISTANCE + 1 : playerchunk.getTicketLevel();

            if (k != j) {
                playerchunk = ChunkMapDistance.this.updateChunkScheduling(i, j, playerchunk, k);
                if (playerchunk != null) {
                    ChunkMapDistance.this.chunksToUpdateFutures.add(playerchunk);
                }

            }
        }

        public int runDistanceUpdates(int i) {
            return this.runUpdates(i);
        }
    }

    private class b extends ChunkMap {

        protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
        protected final int maxDistance;

        protected b(int i) {
            super(i + 2, 16, 256);
            this.maxDistance = i;
            this.chunks.defaultReturnValue((byte) (i + 2));
        }

        @Override
        protected int getLevel(long i) {
            return this.chunks.get(i);
        }

        @Override
        protected void setLevel(long i, int j) {
            byte b0;

            if (j > this.maxDistance) {
                b0 = this.chunks.remove(i);
            } else {
                b0 = this.chunks.put(i, (byte) j);
            }

            this.onLevelChange(i, b0, j);
        }

        protected void onLevelChange(long i, int j, int k) {}

        @Override
        protected int getLevelFromSource(long i) {
            return this.havePlayer(i) ? 0 : Integer.MAX_VALUE;
        }

        private boolean havePlayer(long i) {
            ObjectSet<EntityPlayer> objectset = (ObjectSet) ChunkMapDistance.this.playersPerChunk.get(i);

            return objectset != null && !objectset.isEmpty();
        }

        public void runAllUpdates() {
            this.runUpdates(Integer.MAX_VALUE);
        }

        private void dumpChunks(String s) {
            try {
                FileOutputStream fileoutputstream = new FileOutputStream(new File(s));

                try {
                    ObjectIterator objectiterator = this.chunks.long2ByteEntrySet().iterator();

                    while (objectiterator.hasNext()) {
                        it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry it_unimi_dsi_fastutil_longs_long2bytemap_entry = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry) objectiterator.next();
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(it_unimi_dsi_fastutil_longs_long2bytemap_entry.getLongKey());
                        String s1 = Byte.toString(it_unimi_dsi_fastutil_longs_long2bytemap_entry.getByteValue());

                        fileoutputstream.write((chunkcoordintpair.x + "\t" + chunkcoordintpair.z + "\t" + s1 + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Throwable throwable) {
                    try {
                        fileoutputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                fileoutputstream.close();
            } catch (IOException ioexception) {
                ChunkMapDistance.LOGGER.error(ioexception);
            }

        }
    }

    private class c extends ChunkMapDistance.b {

        private int viewDistance = 0;
        private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
        private final LongSet toUpdate = new LongOpenHashSet();

        protected c(int i) {
            super(i);
            this.queueLevels.defaultReturnValue(i + 2);
        }

        @Override
        protected void onLevelChange(long i, int j, int k) {
            this.toUpdate.add(i);
        }

        public void updateViewDistance(int i) {
            ObjectIterator objectiterator = this.chunks.long2ByteEntrySet().iterator();

            while (objectiterator.hasNext()) {
                it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry it_unimi_dsi_fastutil_longs_long2bytemap_entry = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry) objectiterator.next();
                byte b0 = it_unimi_dsi_fastutil_longs_long2bytemap_entry.getByteValue();
                long j = it_unimi_dsi_fastutil_longs_long2bytemap_entry.getLongKey();

                this.onLevelChange(j, b0, this.haveTicketFor(b0), b0 <= i - 2);
            }

            this.viewDistance = i;
        }

        private void onLevelChange(long i, int j, boolean flag, boolean flag1) {
            if (flag != flag1) {
                Ticket<?> ticket = new Ticket<>(TicketType.PLAYER, ChunkMapDistance.PLAYER_TICKET_LEVEL, new ChunkCoordIntPair(i));

                if (flag1) {
                    ChunkMapDistance.this.ticketThrottlerInput.tell(ChunkTaskQueueSorter.message(() -> {
                        ChunkMapDistance.this.mainThreadExecutor.execute(() -> {
                            if (this.haveTicketFor(this.getLevel(i))) {
                                ChunkMapDistance.this.addTicket(i, ticket);
                                ChunkMapDistance.this.ticketsToRelease.add(i);
                            } else {
                                ChunkMapDistance.this.ticketThrottlerReleaser.tell(ChunkTaskQueueSorter.release(() -> {
                                }, i, false));
                            }

                        });
                    }, i, () -> {
                        return j;
                    }));
                } else {
                    ChunkMapDistance.this.ticketThrottlerReleaser.tell(ChunkTaskQueueSorter.release(() -> {
                        ChunkMapDistance.this.mainThreadExecutor.execute(() -> {
                            ChunkMapDistance.this.removeTicket(i, ticket);
                        });
                    }, i, true));
                }
            }

        }

        @Override
        public void runAllUpdates() {
            super.runAllUpdates();
            if (!this.toUpdate.isEmpty()) {
                LongIterator longiterator = this.toUpdate.iterator();

                while (longiterator.hasNext()) {
                    long i = longiterator.nextLong();
                    int j = this.queueLevels.get(i);
                    int k = this.getLevel(i);

                    if (j != k) {
                        ChunkMapDistance.this.ticketThrottler.onLevelChange(new ChunkCoordIntPair(i), () -> {
                            return this.queueLevels.get(i);
                        }, k, (l) -> {
                            if (l >= this.queueLevels.defaultReturnValue()) {
                                this.queueLevels.remove(i);
                            } else {
                                this.queueLevels.put(i, l);
                            }

                        });
                        this.onLevelChange(i, k, this.haveTicketFor(j), this.haveTicketFor(k));
                    }
                }

                this.toUpdate.clear();
            }

        }

        private boolean haveTicketFor(int i) {
            return i <= this.viewDistance - 2;
        }
    }
}

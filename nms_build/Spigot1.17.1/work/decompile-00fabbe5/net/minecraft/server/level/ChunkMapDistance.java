package net.minecraft.server.level;

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
    static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.a(ChunkStatus.FULL) - 2;
    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    final Long2ObjectMap<ObjectSet<EntityPlayer>> playersPerChunk = new Long2ObjectOpenHashMap();
    public final Long2ObjectOpenHashMap<ArraySetSorted<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
    private final ChunkMapDistance.a ticketTracker = new ChunkMapDistance.a();
    private final ChunkMapDistance.b naturalSpawnChunkCounter = new ChunkMapDistance.b(8);
    private final ChunkMapDistance.c playerTicketManager = new ChunkMapDistance.c(33);
    final Set<PlayerChunk> chunksToUpdateFutures = Sets.newHashSet();
    final ChunkTaskQueueSorter ticketThrottler;
    final Mailbox<ChunkTaskQueueSorter.a<Runnable>> ticketThrottlerInput;
    final Mailbox<ChunkTaskQueueSorter.b> ticketThrottlerReleaser;
    final LongSet ticketsToRelease = new LongOpenHashSet();
    final Executor mainThreadExecutor;
    private long ticketTickCounter;

    protected ChunkMapDistance(Executor executor, Executor executor1) {
        Objects.requireNonNull(executor1);
        Mailbox<Runnable> mailbox = Mailbox.a("player ticket throttler", executor1::execute);
        ChunkTaskQueueSorter chunktaskqueuesorter = new ChunkTaskQueueSorter(ImmutableList.of(mailbox), executor, 4);

        this.ticketThrottler = chunktaskqueuesorter;
        this.ticketThrottlerInput = chunktaskqueuesorter.a(mailbox, true);
        this.ticketThrottlerReleaser = chunktaskqueuesorter.a(mailbox);
        this.mainThreadExecutor = executor1;
    }

    protected void purgeTickets() {
        ++this.ticketTickCounter;
        ObjectIterator objectiterator = this.tickets.long2ObjectEntrySet().fastIterator();

        while (objectiterator.hasNext()) {
            Entry<ArraySetSorted<Ticket<?>>> entry = (Entry) objectiterator.next();

            if (((ArraySetSorted) entry.getValue()).removeIf((ticket) -> {
                return ticket.b(this.ticketTickCounter);
            })) {
                this.ticketTracker.update(entry.getLongKey(), getLowestTicketLevel((ArraySetSorted) entry.getValue()), false);
            }

            if (((ArraySetSorted) entry.getValue()).isEmpty()) {
                objectiterator.remove();
            }
        }

    }

    private static int getLowestTicketLevel(ArraySetSorted<Ticket<?>> arraysetsorted) {
        return !arraysetsorted.isEmpty() ? ((Ticket) arraysetsorted.b()).b() : PlayerChunkMap.MAX_CHUNK_DISTANCE + 1;
    }

    protected abstract boolean a(long i);

    @Nullable
    protected abstract PlayerChunk b(long i);

    @Nullable
    protected abstract PlayerChunk a(long i, int j, @Nullable PlayerChunk playerchunk, int k);

    public boolean a(PlayerChunkMap playerchunkmap) {
        this.naturalSpawnChunkCounter.a();
        this.playerTicketManager.a();
        int i = Integer.MAX_VALUE - this.ticketTracker.a(Integer.MAX_VALUE);
        boolean flag = i != 0;

        if (flag) {
            ;
        }

        if (!this.chunksToUpdateFutures.isEmpty()) {
            this.chunksToUpdateFutures.forEach((playerchunk) -> {
                playerchunk.a(playerchunkmap, this.mainThreadExecutor);
            });
            this.chunksToUpdateFutures.clear();
            return true;
        } else {
            if (!this.ticketsToRelease.isEmpty()) {
                LongIterator longiterator = this.ticketsToRelease.iterator();

                while (longiterator.hasNext()) {
                    long j = longiterator.nextLong();

                    if (this.e(j).stream().anyMatch((ticket) -> {
                        return ticket.getTicketType() == TicketType.PLAYER;
                    })) {
                        PlayerChunk playerchunk = playerchunkmap.getUpdatingChunk(j);

                        if (playerchunk == null) {
                            throw new IllegalStateException();
                        }

                        CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture = playerchunk.b();

                        completablefuture.thenAccept((either) -> {
                            this.mainThreadExecutor.execute(() -> {
                                this.ticketThrottlerReleaser.a(ChunkTaskQueueSorter.a(() -> {
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
        ArraySetSorted<Ticket<?>> arraysetsorted = this.e(i);
        int j = getLowestTicketLevel(arraysetsorted);
        Ticket<?> ticket1 = (Ticket) arraysetsorted.a((Object) ticket);

        ticket1.a(this.ticketTickCounter);
        if (ticket.b() < j) {
            this.ticketTracker.update(i, ticket.b(), true);
        }

    }

    void removeTicket(long i, Ticket<?> ticket) {
        ArraySetSorted<Ticket<?>> arraysetsorted = this.e(i);

        if (arraysetsorted.remove(ticket)) {
            ;
        }

        if (arraysetsorted.isEmpty()) {
            this.tickets.remove(i);
        }

        this.ticketTracker.update(i, getLowestTicketLevel(arraysetsorted), false);
    }

    public <T> void a(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.addTicket(chunkcoordintpair.pair(), new Ticket<>(tickettype, i, t0));
    }

    public <T> void b(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        Ticket<T> ticket = new Ticket<>(tickettype, i, t0);

        this.removeTicket(chunkcoordintpair.pair(), ticket);
    }

    public <T> void addTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.addTicket(chunkcoordintpair.pair(), new Ticket<>(tickettype, 33 - i, t0));
    }

    public <T> void removeTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        Ticket<T> ticket = new Ticket<>(tickettype, 33 - i, t0);

        this.removeTicket(chunkcoordintpair.pair(), ticket);
    }

    private ArraySetSorted<Ticket<?>> e(long i) {
        return (ArraySetSorted) this.tickets.computeIfAbsent(i, (j) -> {
            return ArraySetSorted.a(4);
        });
    }

    protected void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        Ticket<ChunkCoordIntPair> ticket = new Ticket<>(TicketType.FORCED, 31, chunkcoordintpair);

        if (flag) {
            this.addTicket(chunkcoordintpair.pair(), ticket);
        } else {
            this.removeTicket(chunkcoordintpair.pair(), ticket);
        }

    }

    public void a(SectionPosition sectionposition, EntityPlayer entityplayer) {
        long i = sectionposition.r().pair();

        ((ObjectSet) this.playersPerChunk.computeIfAbsent(i, (j) -> {
            return new ObjectOpenHashSet();
        })).add(entityplayer);
        this.naturalSpawnChunkCounter.update(i, 0, true);
        this.playerTicketManager.update(i, 0, true);
    }

    public void b(SectionPosition sectionposition, EntityPlayer entityplayer) {
        long i = sectionposition.r().pair();
        ObjectSet<EntityPlayer> objectset = (ObjectSet) this.playersPerChunk.get(i);

        objectset.remove(entityplayer);
        if (objectset.isEmpty()) {
            this.playersPerChunk.remove(i);
            this.naturalSpawnChunkCounter.update(i, Integer.MAX_VALUE, false);
            this.playerTicketManager.update(i, Integer.MAX_VALUE, false);
        }

    }

    protected String c(long i) {
        ArraySetSorted<Ticket<?>> arraysetsorted = (ArraySetSorted) this.tickets.get(i);
        String s;

        if (arraysetsorted != null && !arraysetsorted.isEmpty()) {
            s = ((Ticket) arraysetsorted.b()).toString();
        } else {
            s = "no_ticket";
        }

        return s;
    }

    protected void a(int i) {
        this.playerTicketManager.a(i);
    }

    public int b() {
        this.naturalSpawnChunkCounter.a();
        return this.naturalSpawnChunkCounter.chunks.size();
    }

    public boolean d(long i) {
        this.naturalSpawnChunkCounter.a();
        return this.naturalSpawnChunkCounter.chunks.containsKey(i);
    }

    public String c() {
        return this.ticketThrottler.a();
    }

    private void a(String s) {
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

                        fileoutputstream.write((chunkcoordintpair.x + "\t" + chunkcoordintpair.z + "\t" + ticket.getTicketType() + "\t" + ticket.b() + "\t\n").getBytes(StandardCharsets.UTF_8));
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

    private class a extends ChunkMap {

        public a() {
            super(PlayerChunkMap.MAX_CHUNK_DISTANCE + 2, 16, 256);
        }

        @Override
        protected int b(long i) {
            ArraySetSorted<Ticket<?>> arraysetsorted = (ArraySetSorted) ChunkMapDistance.this.tickets.get(i);

            return arraysetsorted == null ? Integer.MAX_VALUE : (arraysetsorted.isEmpty() ? Integer.MAX_VALUE : ((Ticket) arraysetsorted.b()).b());
        }

        @Override
        protected int c(long i) {
            if (!ChunkMapDistance.this.a(i)) {
                PlayerChunk playerchunk = ChunkMapDistance.this.b(i);

                if (playerchunk != null) {
                    return playerchunk.getTicketLevel();
                }
            }

            return PlayerChunkMap.MAX_CHUNK_DISTANCE + 1;
        }

        @Override
        protected void a(long i, int j) {
            PlayerChunk playerchunk = ChunkMapDistance.this.b(i);
            int k = playerchunk == null ? PlayerChunkMap.MAX_CHUNK_DISTANCE + 1 : playerchunk.getTicketLevel();

            if (k != j) {
                playerchunk = ChunkMapDistance.this.a(i, j, playerchunk, k);
                if (playerchunk != null) {
                    ChunkMapDistance.this.chunksToUpdateFutures.add(playerchunk);
                }

            }
        }

        public int a(int i) {
            return this.b(i);
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
        protected int c(long i) {
            return this.chunks.get(i);
        }

        @Override
        protected void a(long i, int j) {
            byte b0;

            if (j > this.maxDistance) {
                b0 = this.chunks.remove(i);
            } else {
                b0 = this.chunks.put(i, (byte) j);
            }

            this.a(i, b0, j);
        }

        protected void a(long i, int j, int k) {}

        @Override
        protected int b(long i) {
            return this.d(i) ? 0 : Integer.MAX_VALUE;
        }

        private boolean d(long i) {
            ObjectSet<EntityPlayer> objectset = (ObjectSet) ChunkMapDistance.this.playersPerChunk.get(i);

            return objectset != null && !objectset.isEmpty();
        }

        public void a() {
            this.b(Integer.MAX_VALUE);
        }

        private void a(String s) {
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
        protected void a(long i, int j, int k) {
            this.toUpdate.add(i);
        }

        public void a(int i) {
            ObjectIterator objectiterator = this.chunks.long2ByteEntrySet().iterator();

            while (objectiterator.hasNext()) {
                it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry it_unimi_dsi_fastutil_longs_long2bytemap_entry = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry) objectiterator.next();
                byte b0 = it_unimi_dsi_fastutil_longs_long2bytemap_entry.getByteValue();
                long j = it_unimi_dsi_fastutil_longs_long2bytemap_entry.getLongKey();

                this.a(j, b0, this.c(b0), b0 <= i - 2);
            }

            this.viewDistance = i;
        }

        private void a(long i, int j, boolean flag, boolean flag1) {
            if (flag != flag1) {
                Ticket<?> ticket = new Ticket<>(TicketType.PLAYER, ChunkMapDistance.PLAYER_TICKET_LEVEL, new ChunkCoordIntPair(i));

                if (flag1) {
                    ChunkMapDistance.this.ticketThrottlerInput.a(ChunkTaskQueueSorter.a(() -> {
                        ChunkMapDistance.this.mainThreadExecutor.execute(() -> {
                            if (this.c(this.c(i))) {
                                ChunkMapDistance.this.addTicket(i, ticket);
                                ChunkMapDistance.this.ticketsToRelease.add(i);
                            } else {
                                ChunkMapDistance.this.ticketThrottlerReleaser.a(ChunkTaskQueueSorter.a(() -> {
                                }, i, false));
                            }

                        });
                    }, i, () -> {
                        return j;
                    }));
                } else {
                    ChunkMapDistance.this.ticketThrottlerReleaser.a(ChunkTaskQueueSorter.a(() -> {
                        ChunkMapDistance.this.mainThreadExecutor.execute(() -> {
                            ChunkMapDistance.this.removeTicket(i, ticket);
                        });
                    }, i, true));
                }
            }

        }

        @Override
        public void a() {
            super.a();
            if (!this.toUpdate.isEmpty()) {
                LongIterator longiterator = this.toUpdate.iterator();

                while (longiterator.hasNext()) {
                    long i = longiterator.nextLong();
                    int j = this.queueLevels.get(i);
                    int k = this.c(i);

                    if (j != k) {
                        ChunkMapDistance.this.ticketThrottler.a(new ChunkCoordIntPair(i), () -> {
                            return this.queueLevels.get(i);
                        }, k, (l) -> {
                            if (l >= this.queueLevels.defaultReturnValue()) {
                                this.queueLevels.remove(i);
                            } else {
                                this.queueLevels.put(i, l);
                            }

                        });
                        this.a(i, k, this.c(j), this.c(k));
                    }
                }

                this.toUpdate.clear();
            }

        }

        private boolean c(int i) {
            return i <= this.viewDistance - 2;
        }
    }
}

package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ArraySetSorted;
import net.minecraft.world.level.ChunkCoordIntPair;

public class TickingTracker extends ChunkMap {

    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
    private final Long2ObjectOpenHashMap<ArraySetSorted<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();

    public TickingTracker() {
        super(34, 16, 256);
        this.chunks.defaultReturnValue((byte) 33);
    }

    private ArraySetSorted<Ticket<?>> getTickets(long i) {
        return (ArraySetSorted) this.tickets.computeIfAbsent(i, (j) -> {
            return ArraySetSorted.create(4);
        });
    }

    private int getTicketLevelAt(ArraySetSorted<Ticket<?>> arraysetsorted) {
        return arraysetsorted.isEmpty() ? 34 : ((Ticket) arraysetsorted.first()).getTicketLevel();
    }

    public void addTicket(long i, Ticket<?> ticket) {
        ArraySetSorted<Ticket<?>> arraysetsorted = this.getTickets(i);
        int j = this.getTicketLevelAt(arraysetsorted);

        arraysetsorted.add(ticket);
        if (ticket.getTicketLevel() < j) {
            this.update(i, ticket.getTicketLevel(), true);
        }

    }

    public void removeTicket(long i, Ticket<?> ticket) {
        ArraySetSorted<Ticket<?>> arraysetsorted = this.getTickets(i);

        arraysetsorted.remove(ticket);
        if (arraysetsorted.isEmpty()) {
            this.tickets.remove(i);
        }

        this.update(i, this.getTicketLevelAt(arraysetsorted), false);
    }

    public <T> void addTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.addTicket(chunkcoordintpair.toLong(), new Ticket<>(tickettype, i, t0));
    }

    public <T> void removeTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        Ticket<T> ticket = new Ticket<>(tickettype, i, t0);

        this.removeTicket(chunkcoordintpair.toLong(), ticket);
    }

    public void replacePlayerTicketsLevel(int i) {
        List<Pair<Ticket<ChunkCoordIntPair>, Long>> list = new ArrayList();
        ObjectIterator objectiterator = this.tickets.long2ObjectEntrySet().iterator();

        Ticket ticket;

        while (objectiterator.hasNext()) {
            Entry<ArraySetSorted<Ticket<?>>> entry = (Entry) objectiterator.next();
            Iterator iterator = ((ArraySetSorted) entry.getValue()).iterator();

            while (iterator.hasNext()) {
                ticket = (Ticket) iterator.next();
                if (ticket.getType() == TicketType.PLAYER) {
                    list.add(Pair.of(ticket, entry.getLongKey()));
                }
            }
        }

        Iterator iterator1 = list.iterator();

        while (iterator1.hasNext()) {
            Pair<Ticket<ChunkCoordIntPair>, Long> pair = (Pair) iterator1.next();
            Long olong = (Long) pair.getSecond();

            ticket = (Ticket) pair.getFirst();
            this.removeTicket(olong, ticket);
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(olong);
            TicketType<ChunkCoordIntPair> tickettype = ticket.getType();

            this.addTicket(tickettype, chunkcoordintpair, i, chunkcoordintpair);
        }

    }

    @Override
    protected int getLevelFromSource(long i) {
        ArraySetSorted<Ticket<?>> arraysetsorted = (ArraySetSorted) this.tickets.get(i);

        return arraysetsorted != null && !arraysetsorted.isEmpty() ? ((Ticket) arraysetsorted.first()).getTicketLevel() : Integer.MAX_VALUE;
    }

    public int getLevel(ChunkCoordIntPair chunkcoordintpair) {
        return this.getLevel(chunkcoordintpair.toLong());
    }

    @Override
    protected int getLevel(long i) {
        return this.chunks.get(i);
    }

    @Override
    protected void setLevel(long i, int j) {
        if (j > 33) {
            this.chunks.remove(i);
        } else {
            this.chunks.put(i, (byte) j);
        }

    }

    public void runAllUpdates() {
        this.runUpdates(Integer.MAX_VALUE);
    }

    public String getTicketDebugString(long i) {
        ArraySetSorted<Ticket<?>> arraysetsorted = (ArraySetSorted) this.tickets.get(i);

        return arraysetsorted != null && !arraysetsorted.isEmpty() ? ((Ticket) arraysetsorted.first()).toString() : "no_ticket";
    }
}

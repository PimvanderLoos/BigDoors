package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;

public record NextTickListEntry<T> (T type, BlockPosition pos, long triggerTick, TickListPriority priority, long subTickOrder) {

    public static final Comparator<NextTickListEntry<?>> DRAIN_ORDER = (nextticklistentry, nextticklistentry1) -> {
        int i = Long.compare(nextticklistentry.triggerTick, nextticklistentry1.triggerTick);

        if (i != 0) {
            return i;
        } else {
            i = nextticklistentry.priority.compareTo(nextticklistentry1.priority);
            return i != 0 ? i : Long.compare(nextticklistentry.subTickOrder, nextticklistentry1.subTickOrder);
        }
    };
    public static final Comparator<NextTickListEntry<?>> INTRA_TICK_DRAIN_ORDER = (nextticklistentry, nextticklistentry1) -> {
        int i = nextticklistentry.priority.compareTo(nextticklistentry1.priority);

        return i != 0 ? i : Long.compare(nextticklistentry.subTickOrder, nextticklistentry1.subTickOrder);
    };
    public static final Strategy<NextTickListEntry<?>> UNIQUE_TICK_HASH = new Strategy<NextTickListEntry<?>>() {
        public int hashCode(NextTickListEntry<?> nextticklistentry) {
            return 31 * nextticklistentry.pos().hashCode() + nextticklistentry.type().hashCode();
        }

        public boolean equals(@Nullable NextTickListEntry<?> nextticklistentry, @Nullable NextTickListEntry<?> nextticklistentry1) {
            return nextticklistentry == nextticklistentry1 ? true : (nextticklistentry != null && nextticklistentry1 != null ? nextticklistentry.type() == nextticklistentry1.type() && nextticklistentry.pos().equals(nextticklistentry1.pos()) : false);
        }
    };

    public NextTickListEntry(T t0, BlockPosition blockposition, long i, long j) {
        this(t0, blockposition, i, TickListPriority.NORMAL, j);
    }

    public NextTickListEntry(T t0, BlockPosition blockposition, long i, TickListPriority ticklistpriority, long j) {
        blockposition = blockposition.immutable();
        this.type = t0;
        this.pos = blockposition;
        this.triggerTick = i;
        this.priority = ticklistpriority;
        this.subTickOrder = j;
    }

    public static <T> NextTickListEntry<T> probe(T t0, BlockPosition blockposition) {
        return new NextTickListEntry<>(t0, blockposition, 0L, TickListPriority.NORMAL, 0L);
    }
}

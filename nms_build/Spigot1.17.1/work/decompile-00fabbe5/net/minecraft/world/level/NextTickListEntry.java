package net.minecraft.world.level;

import java.util.Comparator;
import net.minecraft.core.BlockPosition;

public class NextTickListEntry<T> {

    private static long counter;
    private final T type;
    public final BlockPosition pos;
    public final long triggerTick;
    public final TickListPriority priority;
    private final long c;

    public NextTickListEntry(BlockPosition blockposition, T t0) {
        this(blockposition, t0, 0L, TickListPriority.NORMAL);
    }

    public NextTickListEntry(BlockPosition blockposition, T t0, long i, TickListPriority ticklistpriority) {
        this.c = (long) (NextTickListEntry.counter++);
        this.pos = blockposition.immutableCopy();
        this.type = t0;
        this.triggerTick = i;
        this.priority = ticklistpriority;
    }

    public boolean equals(Object object) {
        if (!(object instanceof NextTickListEntry)) {
            return false;
        } else {
            NextTickListEntry<?> nextticklistentry = (NextTickListEntry) object;

            return this.pos.equals(nextticklistentry.pos) && this.type == nextticklistentry.type;
        }
    }

    public int hashCode() {
        return this.pos.hashCode();
    }

    public static <T> Comparator<NextTickListEntry<T>> a() {
        return Comparator.comparingLong((nextticklistentry) -> {
            return nextticklistentry.triggerTick;
        }).thenComparing((nextticklistentry) -> {
            return nextticklistentry.priority;
        }).thenComparingLong((nextticklistentry) -> {
            return nextticklistentry.c;
        });
    }

    public String toString() {
        return this.type + ": " + this.pos + ", " + this.triggerTick + ", " + this.priority + ", " + this.c;
    }

    public T b() {
        return this.type;
    }
}

package net.minecraft.server;

public class NextTickListEntry<T> implements Comparable<NextTickListEntry<T>> {

    private static long d;
    private final T e;
    public final BlockPosition a;
    public final long b;
    public final TickListPriority c;
    private final long f;

    public NextTickListEntry(BlockPosition blockposition, T t0) {
        this(blockposition, t0, 0L, TickListPriority.NORMAL);
    }

    public NextTickListEntry(BlockPosition blockposition, T t0, long i, TickListPriority ticklistpriority) {
        this.f = (long) (NextTickListEntry.d++);
        this.a = blockposition.h();
        this.e = t0;
        this.b = i;
        this.c = ticklistpriority;
    }

    public boolean equals(Object object) {
        if (!(object instanceof NextTickListEntry)) {
            return false;
        } else {
            NextTickListEntry nextticklistentry = (NextTickListEntry) object;

            return this.a.equals(nextticklistentry.a) && this.e == nextticklistentry.e;
        }
    }

    public int hashCode() {
        return this.a.hashCode();
    }

    public int compareTo(NextTickListEntry nextticklistentry) {
        return this.b < nextticklistentry.b ? -1 : (this.b > nextticklistentry.b ? 1 : (this.c.ordinal() < nextticklistentry.c.ordinal() ? -1 : (this.c.ordinal() > nextticklistentry.c.ordinal() ? 1 : (this.f < nextticklistentry.f ? -1 : (this.f > nextticklistentry.f ? 1 : 0)))));
    }

    public String toString() {
        return this.e + ": " + this.a + ", " + this.b + ", " + this.c + ", " + this.f;
    }

    public T a() {
        return this.e;
    }
}

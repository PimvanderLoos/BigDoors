package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTTagLongArray extends NBTBase {

    private long[] b;

    NBTTagLongArray() {}

    public NBTTagLongArray(long[] along) {
        this.b = along;
    }

    public NBTTagLongArray(List<Long> list) {
        this(a(list));
    }

    private static long[] a(List<Long> list) {
        long[] along = new long[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Long olong = (Long) list.get(i);

            along[i] = olong == null ? 0L : olong.longValue();
        }

        return along;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.b.length);
        long[] along = this.b;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along[j];

            dataoutput.writeLong(k);
        }

    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(192L);
        int j = datainput.readInt();

        nbtreadlimiter.a((long) (64 * j));
        this.b = new long[j];

        for (int k = 0; k < j; ++k) {
            this.b[k] = datainput.readLong();
        }

    }

    public byte getTypeId() {
        return (byte) 12;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[L;");

        for (int i = 0; i < this.b.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.b[i]).append('L');
        }

        return stringbuilder.append(']').toString();
    }

    public NBTTagLongArray c() {
        long[] along = new long[this.b.length];

        System.arraycopy(this.b, 0, along, 0, this.b.length);
        return new NBTTagLongArray(along);
    }

    public boolean equals(Object object) {
        return super.equals(object) && Arrays.equals(this.b, ((NBTTagLongArray) object).b);
    }

    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.b);
    }

    public NBTBase clone() {
        return this.c();
    }
}

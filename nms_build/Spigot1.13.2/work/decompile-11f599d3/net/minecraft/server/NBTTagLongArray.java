package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagLongArray extends NBTList<NBTTagLong> {

    private long[] f;

    NBTTagLongArray() {}

    public NBTTagLongArray(long[] along) {
        this.f = along;
    }

    public NBTTagLongArray(LongSet longset) {
        this.f = longset.toLongArray();
    }

    public NBTTagLongArray(List<Long> list) {
        this(a(list));
    }

    private static long[] a(List<Long> list) {
        long[] along = new long[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Long olong = (Long) list.get(i);

            along[i] = olong == null ? 0L : olong;
        }

        return along;
    }

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.f.length);
        long[] along = this.f;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along[j];

            dataoutput.writeLong(k);
        }

    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(192L);
        int j = datainput.readInt();

        nbtreadlimiter.a((long) (64 * j));
        this.f = new long[j];

        for (int k = 0; k < j; ++k) {
            this.f[k] = datainput.readLong();
        }

    }

    public byte getTypeId() {
        return 12;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[L;");

        for (int i = 0; i < this.f.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.f[i]).append('L');
        }

        return stringbuilder.append(']').toString();
    }

    public NBTTagLongArray clone() {
        long[] along = new long[this.f.length];

        System.arraycopy(this.f, 0, along, 0, this.f.length);
        return new NBTTagLongArray(along);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLongArray && Arrays.equals(this.f, ((NBTTagLongArray) object).f);
    }

    public int hashCode() {
        return Arrays.hashCode(this.f);
    }

    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("L")).a(NBTTagLongArray.e);
        IChatBaseComponent ichatbasecomponent1 = (new ChatComponentText("[")).addSibling(ichatbasecomponent).a(";");

        for (int j = 0; j < this.f.length; ++j) {
            IChatBaseComponent ichatbasecomponent2 = (new ChatComponentText(String.valueOf(this.f[j]))).a(NBTTagLongArray.d);

            ichatbasecomponent1.a(" ").addSibling(ichatbasecomponent2).addSibling(ichatbasecomponent);
            if (j != this.f.length - 1) {
                ichatbasecomponent1.a(",");
            }
        }

        ichatbasecomponent1.a("]");
        return ichatbasecomponent1;
    }

    public long[] d() {
        return this.f;
    }

    public int size() {
        return this.f.length;
    }

    public NBTTagLong c(int i) {
        return new NBTTagLong(this.f[i]);
    }

    public void a(int i, NBTBase nbtbase) {
        this.f[i] = ((NBTNumber) nbtbase).asLong();
    }

    public void b(int i) {
        this.f = ArrayUtils.remove(this.f, i);
    }
}

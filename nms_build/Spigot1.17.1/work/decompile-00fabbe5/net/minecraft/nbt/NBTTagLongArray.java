package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagLongArray extends NBTList<NBTTagLong> {

    private static final int SELF_SIZE_IN_BITS = 192;
    public static final NBTTagType<NBTTagLongArray> TYPE = new NBTTagType<NBTTagLongArray>() {
        @Override
        public NBTTagLongArray b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(192L);
            int j = datainput.readInt();

            nbtreadlimiter.a(64L * (long) j);
            long[] along = new long[j];

            for (int k = 0; k < j; ++k) {
                along[k] = datainput.readLong();
            }

            return new NBTTagLongArray(along);
        }

        @Override
        public String a() {
            return "LONG[]";
        }

        @Override
        public String b() {
            return "TAG_Long_Array";
        }
    };
    private long[] data;

    public NBTTagLongArray(long[] along) {
        this.data = along;
    }

    public NBTTagLongArray(LongSet longset) {
        this.data = longset.toLongArray();
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

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data.length);
        long[] along = this.data;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along[j];

            dataoutput.writeLong(k);
        }

    }

    @Override
    public byte getTypeId() {
        return 12;
    }

    @Override
    public NBTTagType<NBTTagLongArray> b() {
        return NBTTagLongArray.TYPE;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public NBTTagLongArray clone() {
        long[] along = new long[this.data.length];

        System.arraycopy(this.data, 0, along, 0, this.data.length);
        return new NBTTagLongArray(along);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLongArray && Arrays.equals(this.data, ((NBTTagLongArray) object).data);
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    public long[] getLongs() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public NBTTagLong get(int i) {
        return NBTTagLong.a(this.data[i]);
    }

    public NBTTagLong set(int i, NBTTagLong nbttaglong) {
        long j = this.data[i];

        this.data[i] = nbttaglong.asLong();
        return NBTTagLong.a(j);
    }

    public void add(int i, NBTTagLong nbttaglong) {
        this.data = ArrayUtils.add(this.data, i, nbttaglong.asLong());
    }

    @Override
    public boolean a(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data[i] = ((NBTNumber) nbtbase).asLong();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean b(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data = ArrayUtils.add(this.data, i, ((NBTNumber) nbtbase).asLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagLong remove(int i) {
        long j = this.data[i];

        this.data = ArrayUtils.remove(this.data, i);
        return NBTTagLong.a(j);
    }

    @Override
    public byte e() {
        return 4;
    }

    public void clear() {
        this.data = new long[0];
    }
}

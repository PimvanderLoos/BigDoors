package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagLongArray extends NBTList<NBTTagLong> {

    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final NBTTagType<NBTTagLongArray> TYPE = new NBTTagType.b<NBTTagLongArray>() {
        @Override
        public NBTTagLongArray load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(24L);
            int j = datainput.readInt();

            nbtreadlimiter.accountBytes(8L * (long) j);
            long[] along = new long[j];

            for (int k = 0; k < j; ++k) {
                along[k] = datainput.readLong();
            }

            return new NBTTagLongArray(along);
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            int i = datainput.readInt();
            long[] along = new long[i];

            for (int j = 0; j < i; ++j) {
                along[j] = datainput.readLong();
            }

            return streamtagvisitor.visit(along);
        }

        @Override
        public void skip(DataInput datainput) throws IOException {
            datainput.skipBytes(datainput.readInt() * 8);
        }

        @Override
        public String getName() {
            return "LONG[]";
        }

        @Override
        public String getPrettyName() {
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
        this(toArray(list));
    }

    private static long[] toArray(List<Long> list) {
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
    public int sizeInBytes() {
        return 24 + 8 * this.data.length;
    }

    @Override
    public byte getId() {
        return 12;
    }

    @Override
    public NBTTagType<NBTTagLongArray> getType() {
        return NBTTagLongArray.TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public NBTTagLongArray copy() {
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
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitLongArray(this);
    }

    public long[] getAsLongArray() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public NBTTagLong get(int i) {
        return NBTTagLong.valueOf(this.data[i]);
    }

    public NBTTagLong set(int i, NBTTagLong nbttaglong) {
        long j = this.data[i];

        this.data[i] = nbttaglong.getAsLong();
        return NBTTagLong.valueOf(j);
    }

    public void add(int i, NBTTagLong nbttaglong) {
        this.data = ArrayUtils.add(this.data, i, nbttaglong.getAsLong());
    }

    @Override
    public boolean setTag(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data[i] = ((NBTNumber) nbtbase).getAsLong();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data = ArrayUtils.add(this.data, i, ((NBTNumber) nbtbase).getAsLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagLong remove(int i) {
        long j = this.data[i];

        this.data = ArrayUtils.remove(this.data, i);
        return NBTTagLong.valueOf(j);
    }

    @Override
    public byte getElementType() {
        return 4;
    }

    public void clear() {
        this.data = new long[0];
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        return streamtagvisitor.visit(this.data);
    }
}

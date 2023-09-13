package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTNumber {

    private static final int SELF_SIZE_IN_BYTES = 16;
    public static final NBTTagType<NBTTagLong> TYPE = new NBTTagType.a<NBTTagLong>() {
        @Override
        public NBTTagLong load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(16L);
            return NBTTagLong.valueOf(datainput.readLong());
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            return streamtagvisitor.visit(datainput.readLong());
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public String getName() {
            return "LONG";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Long";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private final long data;

    NBTTagLong(long i) {
        this.data = i;
    }

    public static NBTTagLong valueOf(long i) {
        return i >= -128L && i <= 1024L ? NBTTagLong.a.cache[(int) i - -128] : new NBTTagLong(i);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeLong(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 16;
    }

    @Override
    public byte getId() {
        return 4;
    }

    @Override
    public NBTTagType<NBTTagLong> getType() {
        return NBTTagLong.TYPE;
    }

    @Override
    public NBTTagLong copy() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLong && this.data == ((NBTTagLong) object).data;
    }

    public int hashCode() {
        return (int) (this.data ^ this.data >>> 32);
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitLong(this);
    }

    @Override
    public long getAsLong() {
        return this.data;
    }

    @Override
    public int getAsInt() {
        return (int) (this.data & -1L);
    }

    @Override
    public short getAsShort() {
        return (short) ((int) (this.data & 65535L));
    }

    @Override
    public byte getAsByte() {
        return (byte) ((int) (this.data & 255L));
    }

    @Override
    public double getAsDouble() {
        return (double) this.data;
    }

    @Override
    public float getAsFloat() {
        return (float) this.data;
    }

    @Override
    public Number getAsNumber() {
        return this.data;
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        return streamtagvisitor.visit(this.data);
    }

    private static class a {

        private static final int HIGH = 1024;
        private static final int LOW = -128;
        static final NBTTagLong[] cache = new NBTTagLong[1153];

        private a() {}

        static {
            for (int i = 0; i < NBTTagLong.a.cache.length; ++i) {
                NBTTagLong.a.cache[i] = new NBTTagLong((long) (-128 + i));
            }

        }
    }
}

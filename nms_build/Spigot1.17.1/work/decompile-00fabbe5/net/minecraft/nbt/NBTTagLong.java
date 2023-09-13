package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTNumber {

    private static final int SELF_SIZE_IN_BITS = 128;
    public static final NBTTagType<NBTTagLong> TYPE = new NBTTagType<NBTTagLong>() {
        @Override
        public NBTTagLong b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(128L);
            return NBTTagLong.a(datainput.readLong());
        }

        @Override
        public String a() {
            return "LONG";
        }

        @Override
        public String b() {
            return "TAG_Long";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final long data;

    NBTTagLong(long i) {
        this.data = i;
    }

    public static NBTTagLong a(long i) {
        return i >= -128L && i <= 1024L ? NBTTagLong.a.cache[(int) i - -128] : new NBTTagLong(i);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeLong(this.data);
    }

    @Override
    public byte getTypeId() {
        return 4;
    }

    @Override
    public NBTTagType<NBTTagLong> b() {
        return NBTTagLong.TYPE;
    }

    @Override
    public NBTTagLong clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLong && this.data == ((NBTTagLong) object).data;
    }

    public int hashCode() {
        return (int) (this.data ^ this.data >>> 32);
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    @Override
    public long asLong() {
        return this.data;
    }

    @Override
    public int asInt() {
        return (int) (this.data & -1L);
    }

    @Override
    public short asShort() {
        return (short) ((int) (this.data & 65535L));
    }

    @Override
    public byte asByte() {
        return (byte) ((int) (this.data & 255L));
    }

    @Override
    public double asDouble() {
        return (double) this.data;
    }

    @Override
    public float asFloat() {
        return (float) this.data;
    }

    @Override
    public Number k() {
        return this.data;
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

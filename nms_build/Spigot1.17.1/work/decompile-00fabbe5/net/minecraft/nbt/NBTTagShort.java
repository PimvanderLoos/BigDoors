package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTNumber {

    private static final int SELF_SIZE_IN_BITS = 80;
    public static final NBTTagType<NBTTagShort> TYPE = new NBTTagType<NBTTagShort>() {
        @Override
        public NBTTagShort b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(80L);
            return NBTTagShort.a(datainput.readShort());
        }

        @Override
        public String a() {
            return "SHORT";
        }

        @Override
        public String b() {
            return "TAG_Short";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final short data;

    NBTTagShort(short short0) {
        this.data = short0;
    }

    public static NBTTagShort a(short short0) {
        return short0 >= -128 && short0 <= 1024 ? NBTTagShort.a.cache[short0 - -128] : new NBTTagShort(short0);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeShort(this.data);
    }

    @Override
    public byte getTypeId() {
        return 2;
    }

    @Override
    public NBTTagType<NBTTagShort> b() {
        return NBTTagShort.TYPE;
    }

    @Override
    public NBTTagShort clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagShort && this.data == ((NBTTagShort) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    @Override
    public long asLong() {
        return (long) this.data;
    }

    @Override
    public int asInt() {
        return this.data;
    }

    @Override
    public short asShort() {
        return this.data;
    }

    @Override
    public byte asByte() {
        return (byte) (this.data & 255);
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
        static final NBTTagShort[] cache = new NBTTagShort[1153];

        private a() {}

        static {
            for (int i = 0; i < NBTTagShort.a.cache.length; ++i) {
                NBTTagShort.a.cache[i] = new NBTTagShort((short) (-128 + i));
            }

        }
    }
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTNumber {

    private static final int SELF_SIZE_IN_BITS = 96;
    public static final NBTTagType<NBTTagInt> TYPE = new NBTTagType<NBTTagInt>() {
        @Override
        public NBTTagInt b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(96L);
            return NBTTagInt.a(datainput.readInt());
        }

        @Override
        public String a() {
            return "INT";
        }

        @Override
        public String b() {
            return "TAG_Int";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final int data;

    NBTTagInt(int i) {
        this.data = i;
    }

    public static NBTTagInt a(int i) {
        return i >= -128 && i <= 1024 ? NBTTagInt.a.cache[i - -128] : new NBTTagInt(i);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data);
    }

    @Override
    public byte getTypeId() {
        return 3;
    }

    @Override
    public NBTTagType<NBTTagInt> b() {
        return NBTTagInt.TYPE;
    }

    @Override
    public NBTTagInt clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagInt && this.data == ((NBTTagInt) object).data;
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
        return (short) (this.data & '\uffff');
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
        static final NBTTagInt[] cache = new NBTTagInt[1153];

        private a() {}

        static {
            for (int i = 0; i < NBTTagInt.a.cache.length; ++i) {
                NBTTagInt.a.cache[i] = new NBTTagInt(-128 + i);
            }

        }
    }
}

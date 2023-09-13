package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTNumber {

    private static final int SELF_SIZE_IN_BITS = 72;
    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagType<NBTTagByte>() {
        @Override
        public NBTTagByte b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(72L);
            return NBTTagByte.a(datainput.readByte());
        }

        @Override
        public String a() {
            return "BYTE";
        }

        @Override
        public String b() {
            return "TAG_Byte";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    public static final NBTTagByte ZERO = a((byte) 0);
    public static final NBTTagByte ONE = a((byte) 1);
    private final byte data;

    NBTTagByte(byte b0) {
        this.data = b0;
    }

    public static NBTTagByte a(byte b0) {
        return NBTTagByte.a.cache[128 + b0];
    }

    public static NBTTagByte a(boolean flag) {
        return flag ? NBTTagByte.ONE : NBTTagByte.ZERO;
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(this.data);
    }

    @Override
    public byte getTypeId() {
        return 1;
    }

    @Override
    public NBTTagType<NBTTagByte> b() {
        return NBTTagByte.TYPE;
    }

    @Override
    public NBTTagByte clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagByte && this.data == ((NBTTagByte) object).data;
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
        return (short) this.data;
    }

    @Override
    public byte asByte() {
        return this.data;
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

        static final NBTTagByte[] cache = new NBTTagByte[256];

        private a() {}

        static {
            for (int i = 0; i < NBTTagByte.a.cache.length; ++i) {
                NBTTagByte.a.cache[i] = new NBTTagByte((byte) (i - 128));
            }

        }
    }
}

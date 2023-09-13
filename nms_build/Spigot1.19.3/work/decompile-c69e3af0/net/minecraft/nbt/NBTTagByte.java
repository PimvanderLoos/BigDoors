package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTNumber {

    private static final int SELF_SIZE_IN_BYTES = 9;
    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagType.a<NBTTagByte>() {
        @Override
        public NBTTagByte load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(9L);
            return NBTTagByte.valueOf(datainput.readByte());
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            return streamtagvisitor.visit(datainput.readByte());
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public String getName() {
            return "BYTE";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Byte";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    public static final NBTTagByte ZERO = valueOf((byte) 0);
    public static final NBTTagByte ONE = valueOf((byte) 1);
    private final byte data;

    NBTTagByte(byte b0) {
        this.data = b0;
    }

    public static NBTTagByte valueOf(byte b0) {
        return NBTTagByte.a.cache[128 + b0];
    }

    public static NBTTagByte valueOf(boolean flag) {
        return flag ? NBTTagByte.ONE : NBTTagByte.ZERO;
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 9;
    }

    @Override
    public byte getId() {
        return 1;
    }

    @Override
    public NBTTagType<NBTTagByte> getType() {
        return NBTTagByte.TYPE;
    }

    @Override
    public NBTTagByte copy() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagByte && this.data == ((NBTTagByte) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitByte(this);
    }

    @Override
    public long getAsLong() {
        return (long) this.data;
    }

    @Override
    public int getAsInt() {
        return this.data;
    }

    @Override
    public short getAsShort() {
        return (short) this.data;
    }

    @Override
    public byte getAsByte() {
        return this.data;
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

        static final NBTTagByte[] cache = new NBTTagByte[256];

        private a() {}

        static {
            for (int i = 0; i < NBTTagByte.a.cache.length; ++i) {
                NBTTagByte.a.cache[i] = new NBTTagByte((byte) (i - 128));
            }

        }
    }
}

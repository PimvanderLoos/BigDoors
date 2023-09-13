package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTNumber {

    private static final int SELF_SIZE_IN_BYTES = 10;
    public static final NBTTagType<NBTTagShort> TYPE = new NBTTagType.a<NBTTagShort>() {
        @Override
        public NBTTagShort load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(10L);
            return NBTTagShort.valueOf(datainput.readShort());
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            return streamtagvisitor.visit(datainput.readShort());
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public String getName() {
            return "SHORT";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Short";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private final short data;

    NBTTagShort(short short0) {
        this.data = short0;
    }

    public static NBTTagShort valueOf(short short0) {
        return short0 >= -128 && short0 <= 1024 ? NBTTagShort.a.cache[short0 - -128] : new NBTTagShort(short0);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeShort(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 10;
    }

    @Override
    public byte getId() {
        return 2;
    }

    @Override
    public NBTTagType<NBTTagShort> getType() {
        return NBTTagShort.TYPE;
    }

    @Override
    public NBTTagShort copy() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagShort && this.data == ((NBTTagShort) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitShort(this);
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
        return this.data;
    }

    @Override
    public byte getAsByte() {
        return (byte) (this.data & 255);
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
        static final NBTTagShort[] cache = new NBTTagShort[1153];

        private a() {}

        static {
            for (int i = 0; i < NBTTagShort.a.cache.length; ++i) {
                NBTTagShort.a.cache[i] = new NBTTagShort((short) (-128 + i));
            }

        }
    }
}

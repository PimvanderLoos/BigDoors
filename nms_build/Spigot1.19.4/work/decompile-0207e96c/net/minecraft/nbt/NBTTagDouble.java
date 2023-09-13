package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTNumber {

    private static final int SELF_SIZE_IN_BYTES = 16;
    public static final NBTTagDouble ZERO = new NBTTagDouble(0.0D);
    public static final NBTTagType<NBTTagDouble> TYPE = new NBTTagType.a<NBTTagDouble>() {
        @Override
        public NBTTagDouble load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(16L);
            return NBTTagDouble.valueOf(datainput.readDouble());
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            return streamtagvisitor.visit(datainput.readDouble());
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public String getName() {
            return "DOUBLE";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Double";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private final double data;

    private NBTTagDouble(double d0) {
        this.data = d0;
    }

    public static NBTTagDouble valueOf(double d0) {
        return d0 == 0.0D ? NBTTagDouble.ZERO : new NBTTagDouble(d0);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeDouble(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 16;
    }

    @Override
    public byte getId() {
        return 6;
    }

    @Override
    public NBTTagType<NBTTagDouble> getType() {
        return NBTTagDouble.TYPE;
    }

    @Override
    public NBTTagDouble copy() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagDouble && this.data == ((NBTTagDouble) object).data;
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.data);

        return (int) (i ^ i >>> 32);
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitDouble(this);
    }

    @Override
    public long getAsLong() {
        return (long) Math.floor(this.data);
    }

    @Override
    public int getAsInt() {
        return MathHelper.floor(this.data);
    }

    @Override
    public short getAsShort() {
        return (short) (MathHelper.floor(this.data) & '\uffff');
    }

    @Override
    public byte getAsByte() {
        return (byte) (MathHelper.floor(this.data) & 255);
    }

    @Override
    public double getAsDouble() {
        return this.data;
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
}

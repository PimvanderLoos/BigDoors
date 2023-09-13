package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTNumber {

    private static final int SELF_SIZE_IN_BYTES = 12;
    public static final NBTTagFloat ZERO = new NBTTagFloat(0.0F);
    public static final NBTTagType<NBTTagFloat> TYPE = new NBTTagType.a<NBTTagFloat>() {
        @Override
        public NBTTagFloat load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(12L);
            return NBTTagFloat.valueOf(datainput.readFloat());
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            return streamtagvisitor.visit(datainput.readFloat());
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public String getName() {
            return "FLOAT";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Float";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private final float data;

    private NBTTagFloat(float f) {
        this.data = f;
    }

    public static NBTTagFloat valueOf(float f) {
        return f == 0.0F ? NBTTagFloat.ZERO : new NBTTagFloat(f);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeFloat(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 12;
    }

    @Override
    public byte getId() {
        return 5;
    }

    @Override
    public NBTTagType<NBTTagFloat> getType() {
        return NBTTagFloat.TYPE;
    }

    @Override
    public NBTTagFloat copy() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagFloat && this.data == ((NBTTagFloat) object).data;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.data);
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitFloat(this);
    }

    @Override
    public long getAsLong() {
        return (long) this.data;
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
        return (double) this.data;
    }

    @Override
    public float getAsFloat() {
        return this.data;
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

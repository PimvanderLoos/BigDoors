package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTNumber {

    private float data;

    NBTTagFloat() {}

    public NBTTagFloat(float f) {
        this.data = f;
    }

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeFloat(this.data);
    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(96L);
        this.data = datainput.readFloat();
    }

    public byte getTypeId() {
        return 5;
    }

    public String toString() {
        return this.data + "f";
    }

    public NBTTagFloat clone() {
        return new NBTTagFloat(this.data);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagFloat && this.data == ((NBTTagFloat) object).data;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.data);
    }

    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("f")).a(NBTTagFloat.e);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatbasecomponent).a(NBTTagFloat.d);
    }

    public long asLong() {
        return (long) this.data;
    }

    public int asInt() {
        return MathHelper.d(this.data);
    }

    public short asShort() {
        return (short) (MathHelper.d(this.data) & '\uffff');
    }

    public byte asByte() {
        return (byte) (MathHelper.d(this.data) & 255);
    }

    public double asDouble() {
        return (double) this.data;
    }

    public float asFloat() {
        return this.data;
    }

    public Number j() {
        return this.data;
    }
}

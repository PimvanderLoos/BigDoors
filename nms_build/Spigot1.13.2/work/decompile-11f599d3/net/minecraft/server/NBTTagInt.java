package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTNumber {

    private int data;

    NBTTagInt() {}

    public NBTTagInt(int i) {
        this.data = i;
    }

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data);
    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(96L);
        this.data = datainput.readInt();
    }

    public byte getTypeId() {
        return 3;
    }

    public String toString() {
        return String.valueOf(this.data);
    }

    public NBTTagInt clone() {
        return new NBTTagInt(this.data);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagInt && this.data == ((NBTTagInt) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    public IChatBaseComponent a(String s, int i) {
        return (new ChatComponentText(String.valueOf(this.data))).a(NBTTagInt.d);
    }

    public long asLong() {
        return (long) this.data;
    }

    public int asInt() {
        return this.data;
    }

    public short asShort() {
        return (short) (this.data & '\uffff');
    }

    public byte asByte() {
        return (byte) (this.data & 255);
    }

    public double asDouble() {
        return (double) this.data;
    }

    public float asFloat() {
        return (float) this.data;
    }

    public Number j() {
        return this.data;
    }
}

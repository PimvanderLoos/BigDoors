package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTNumber {

    private short data;

    public NBTTagShort() {}

    public NBTTagShort(short short0) {
        this.data = short0;
    }

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeShort(this.data);
    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(80L);
        this.data = datainput.readShort();
    }

    public byte getTypeId() {
        return 2;
    }

    public String toString() {
        return this.data + "s";
    }

    public NBTTagShort clone() {
        return new NBTTagShort(this.data);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagShort && this.data == ((NBTTagShort) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("s")).a(NBTTagShort.e);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatbasecomponent).a(NBTTagShort.d);
    }

    public long asLong() {
        return (long) this.data;
    }

    public int asInt() {
        return this.data;
    }

    public short asShort() {
        return this.data;
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

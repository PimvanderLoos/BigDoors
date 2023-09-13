package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTNumber {

    private byte data;

    NBTTagByte() {}

    public NBTTagByte(byte b0) {
        this.data = b0;
    }

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(this.data);
    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(72L);
        this.data = datainput.readByte();
    }

    public byte getTypeId() {
        return (byte) 1;
    }

    public String toString() {
        return this.data + "b";
    }

    public NBTTagByte c() {
        return new NBTTagByte(this.data);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagByte && this.data == ((NBTTagByte) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("b")).a(NBTTagByte.e);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatbasecomponent).a(NBTTagByte.d);
    }

    public long d() {
        return (long) this.data;
    }

    public int e() {
        return this.data;
    }

    public short f() {
        return (short) this.data;
    }

    public byte g() {
        return this.data;
    }

    public double asDouble() {
        return (double) this.data;
    }

    public float i() {
        return (float) this.data;
    }

    public Number j() {
        return Byte.valueOf(this.data);
    }

    public NBTBase clone() {
        return this.c();
    }
}

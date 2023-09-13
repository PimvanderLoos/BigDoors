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

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeByte(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
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
        return super.equals(object) && this.data == ((NBTTagByte) object).data;
    }

    public int hashCode() {
        return super.hashCode() ^ this.data;
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

    public NBTBase clone() {
        return this.c();
    }
}

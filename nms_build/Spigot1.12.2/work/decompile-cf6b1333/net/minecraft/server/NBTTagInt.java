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

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(96L);
        this.data = datainput.readInt();
    }

    public byte getTypeId() {
        return (byte) 3;
    }

    public String toString() {
        return String.valueOf(this.data);
    }

    public NBTTagInt c() {
        return new NBTTagInt(this.data);
    }

    public boolean equals(Object object) {
        return super.equals(object) && this.data == ((NBTTagInt) object).data;
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
        return (short) (this.data & '\uffff');
    }

    public byte g() {
        return (byte) (this.data & 255);
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

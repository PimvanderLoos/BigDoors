package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTNumber {

    private long data;

    NBTTagLong() {}

    public NBTTagLong(long i) {
        this.data = i;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeLong(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(128L);
        this.data = datainput.readLong();
    }

    public byte getTypeId() {
        return (byte) 4;
    }

    public String toString() {
        return this.data + "L";
    }

    public NBTTagLong c() {
        return new NBTTagLong(this.data);
    }

    public boolean equals(Object object) {
        return super.equals(object) && this.data == ((NBTTagLong) object).data;
    }

    public int hashCode() {
        return super.hashCode() ^ (int) (this.data ^ this.data >>> 32);
    }

    public long d() {
        return this.data;
    }

    public int e() {
        return (int) (this.data & -1L);
    }

    public short f() {
        return (short) ((int) (this.data & 65535L));
    }

    public byte g() {
        return (byte) ((int) (this.data & 255L));
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

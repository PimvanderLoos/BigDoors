package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTNumber {

    private double data;

    NBTTagDouble() {}

    public NBTTagDouble(double d0) {
        this.data = d0;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeDouble(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(128L);
        this.data = datainput.readDouble();
    }

    public byte getTypeId() {
        return (byte) 6;
    }

    public String toString() {
        return this.data + "d";
    }

    public NBTTagDouble c() {
        return new NBTTagDouble(this.data);
    }

    public boolean equals(Object object) {
        return super.equals(object) && this.data == ((NBTTagDouble) object).data;
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.data);

        return super.hashCode() ^ (int) (i ^ i >>> 32);
    }

    public long d() {
        return (long) Math.floor(this.data);
    }

    public int e() {
        return MathHelper.floor(this.data);
    }

    public short f() {
        return (short) (MathHelper.floor(this.data) & '\uffff');
    }

    public byte g() {
        return (byte) (MathHelper.floor(this.data) & 255);
    }

    public double asDouble() {
        return this.data;
    }

    public float i() {
        return (float) this.data;
    }

    public NBTBase clone() {
        return this.c();
    }
}

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

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeLong(this.data);
    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(128L);
        this.data = datainput.readLong();
    }

    public byte getTypeId() {
        return 4;
    }

    public String toString() {
        return this.data + "L";
    }

    public NBTTagLong clone() {
        return new NBTTagLong(this.data);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLong && this.data == ((NBTTagLong) object).data;
    }

    public int hashCode() {
        return (int) (this.data ^ this.data >>> 32);
    }

    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("L")).a(NBTTagLong.e);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatbasecomponent).a(NBTTagLong.d);
    }

    public long asLong() {
        return this.data;
    }

    public int asInt() {
        return (int) (this.data & -1L);
    }

    public short asShort() {
        return (short) ((int) (this.data & 65535L));
    }

    public byte asByte() {
        return (byte) ((int) (this.data & 255L));
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

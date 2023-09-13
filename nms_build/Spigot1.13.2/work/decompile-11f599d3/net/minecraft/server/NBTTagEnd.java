package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd implements NBTBase {

    public NBTTagEnd() {}

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(64L);
    }

    public void write(DataOutput dataoutput) throws IOException {}

    public byte getTypeId() {
        return 0;
    }

    public String toString() {
        return "END";
    }

    public NBTTagEnd clone() {
        return new NBTTagEnd();
    }

    public IChatBaseComponent a(String s, int i) {
        return new ChatComponentText("");
    }

    public boolean equals(Object object) {
        return object instanceof NBTTagEnd;
    }

    public int hashCode() {
        return this.getTypeId();
    }
}

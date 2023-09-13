package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NBTTagString extends NBTBase {

    private String data;

    public NBTTagString() {
        this("");
    }

    public NBTTagString(String s) {
        Objects.requireNonNull(s, "Null string not allowed");
        this.data = s;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeUTF(this.data);
    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(288L);
        this.data = datainput.readUTF();
        nbtreadlimiter.a((long) (16 * this.data.length()));
    }

    public byte getTypeId() {
        return (byte) 8;
    }

    public String toString() {
        return a(this.data);
    }

    public NBTTagString c() {
        return new NBTTagString(this.data);
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        } else {
            NBTTagString nbttagstring = (NBTTagString) object;

            return this.data == null && nbttagstring.data == null || Objects.equals(this.data, nbttagstring.data);
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.data.hashCode();
    }

    public String c_() {
        return this.data;
    }

    public static String a(String s) {
        StringBuilder stringbuilder = new StringBuilder("\"");

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 == 92 || c0 == 34) {
                stringbuilder.append('\\');
            }

            stringbuilder.append(c0);
        }

        return stringbuilder.append('\"').toString();
    }

    public NBTBase clone() {
        return this.c();
    }
}

package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NBTTagString implements NBTBase {

    private String data;

    public NBTTagString() {
        this("");
    }

    public NBTTagString(String s) {
        Objects.requireNonNull(s, "Null string not allowed");
        this.data = s;
    }

    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeUTF(this.data);
    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(288L);
        this.data = datainput.readUTF();
        nbtreadlimiter.a((long) (16 * this.data.length()));
    }

    public byte getTypeId() {
        return 8;
    }

    public String toString() {
        return a(this.data, true);
    }

    public NBTTagString clone() {
        return new NBTTagString(this.data);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagString && Objects.equals(this.data, ((NBTTagString) object).data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public String asString() {
        return this.data;
    }

    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText(a(this.data, false))).a(NBTTagString.c);

        return (new ChatComponentText("\"")).addSibling(ichatbasecomponent).a("\"");
    }

    public static String a(String s, boolean flag) {
        StringBuilder stringbuilder = new StringBuilder();

        if (flag) {
            stringbuilder.append('"');
        }

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 == '\\' || c0 == '"') {
                stringbuilder.append('\\');
            }

            stringbuilder.append(c0);
        }

        if (flag) {
            stringbuilder.append('"');
        }

        return stringbuilder.toString();
    }
}

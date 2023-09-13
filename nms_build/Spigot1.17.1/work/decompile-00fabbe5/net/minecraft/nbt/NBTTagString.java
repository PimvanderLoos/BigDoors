package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NBTTagString implements NBTBase {

    private static final int SELF_SIZE_IN_BITS = 288;
    public static final NBTTagType<NBTTagString> TYPE = new NBTTagType<NBTTagString>() {
        @Override
        public NBTTagString b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(288L);
            String s = datainput.readUTF();

            nbtreadlimiter.a((long) (16 * s.length()));
            return NBTTagString.a(s);
        }

        @Override
        public String a() {
            return "STRING";
        }

        @Override
        public String b() {
            return "TAG_String";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private static final NBTTagString EMPTY = new NBTTagString("");
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char ESCAPE = '\\';
    private static final char NOT_SET = '\u0000';
    private final String data;

    private NBTTagString(String s) {
        Objects.requireNonNull(s, "Null string not allowed");
        this.data = s;
    }

    public static NBTTagString a(String s) {
        return s.isEmpty() ? NBTTagString.EMPTY : new NBTTagString(s);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeUTF(this.data);
    }

    @Override
    public byte getTypeId() {
        return 8;
    }

    @Override
    public NBTTagType<NBTTagString> b() {
        return NBTTagString.TYPE;
    }

    @Override
    public String toString() {
        return NBTBase.super.asString();
    }

    @Override
    public NBTTagString clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagString && Objects.equals(this.data, ((NBTTagString) object).data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public String asString() {
        return this.data;
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    public static String b(String s) {
        StringBuilder stringbuilder = new StringBuilder(" ");
        int i = 0;

        for (int j = 0; j < s.length(); ++j) {
            char c0 = s.charAt(j);

            if (c0 == '\\') {
                stringbuilder.append('\\');
            } else if (c0 == '"' || c0 == '\'') {
                if (i == 0) {
                    i = c0 == '"' ? 39 : 34;
                }

                if (i == c0) {
                    stringbuilder.append('\\');
                }
            }

            stringbuilder.append(c0);
        }

        if (i == 0) {
            i = 34;
        }

        stringbuilder.setCharAt(0, (char) i);
        stringbuilder.append((char) i);
        return stringbuilder.toString();
    }
}

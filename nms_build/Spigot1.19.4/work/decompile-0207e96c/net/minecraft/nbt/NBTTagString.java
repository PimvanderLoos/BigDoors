package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Objects;
import net.minecraft.SystemUtils;

public class NBTTagString implements NBTBase {

    private static final int SELF_SIZE_IN_BYTES = 36;
    public static final NBTTagType<NBTTagString> TYPE = new NBTTagType.b<NBTTagString>() {
        @Override
        public NBTTagString load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(36L);
            String s = datainput.readUTF();

            nbtreadlimiter.accountBytes((long) (2 * s.length()));
            return NBTTagString.valueOf(s);
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            return streamtagvisitor.visit(datainput.readUTF());
        }

        @Override
        public void skip(DataInput datainput) throws IOException {
            NBTTagString.skipString(datainput);
        }

        @Override
        public String getName() {
            return "STRING";
        }

        @Override
        public String getPrettyName() {
            return "TAG_String";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private static final NBTTagString EMPTY = new NBTTagString("");
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char ESCAPE = '\\';
    private static final char NOT_SET = '\u0000';
    private final String data;

    public static void skipString(DataInput datainput) throws IOException {
        datainput.skipBytes(datainput.readUnsignedShort());
    }

    private NBTTagString(String s) {
        Objects.requireNonNull(s, "Null string not allowed");
        this.data = s;
    }

    public static NBTTagString valueOf(String s) {
        return s.isEmpty() ? NBTTagString.EMPTY : new NBTTagString(s);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        try {
            dataoutput.writeUTF(this.data);
        } catch (UTFDataFormatException utfdataformatexception) {
            SystemUtils.logAndPauseIfInIde("Failed to write NBT String", utfdataformatexception);
            dataoutput.writeUTF("");
        }

    }

    @Override
    public int sizeInBytes() {
        return 36 + 2 * this.data.length();
    }

    @Override
    public byte getId() {
        return 8;
    }

    @Override
    public NBTTagType<NBTTagString> getType() {
        return NBTTagString.TYPE;
    }

    @Override
    public String toString() {
        return NBTBase.super.getAsString();
    }

    @Override
    public NBTTagString copy() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagString && Objects.equals(this.data, ((NBTTagString) object).data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public String getAsString() {
        return this.data;
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitString(this);
    }

    public static String quoteAndEscape(String s) {
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

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        return streamtagvisitor.visit(this.data);
    }
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd implements NBTBase {

    private static final int SELF_SIZE_IN_BITS = 64;
    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagType<NBTTagEnd>() {
        @Override
        public NBTTagEnd b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
            nbtreadlimiter.a(64L);
            return NBTTagEnd.INSTANCE;
        }

        @Override
        public String a() {
            return "END";
        }

        @Override
        public String b() {
            return "TAG_End";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    public static final NBTTagEnd INSTANCE = new NBTTagEnd();

    private NBTTagEnd() {}

    @Override
    public void write(DataOutput dataoutput) throws IOException {}

    @Override
    public byte getTypeId() {
        return 0;
    }

    @Override
    public NBTTagType<NBTTagEnd> b() {
        return NBTTagEnd.TYPE;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public NBTTagEnd clone() {
        return this;
    }

    @Override
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }
}

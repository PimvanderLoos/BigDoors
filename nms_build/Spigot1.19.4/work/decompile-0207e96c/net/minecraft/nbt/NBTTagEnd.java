package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd implements NBTBase {

    private static final int SELF_SIZE_IN_BYTES = 8;
    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagType<NBTTagEnd>() {
        @Override
        public NBTTagEnd load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
            nbtreadlimiter.accountBytes(8L);
            return NBTTagEnd.INSTANCE;
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) {
            return streamtagvisitor.visitEnd();
        }

        @Override
        public void skip(DataInput datainput, int i) {}

        @Override
        public void skip(DataInput datainput) {}

        @Override
        public String getName() {
            return "END";
        }

        @Override
        public String getPrettyName() {
            return "TAG_End";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    public static final NBTTagEnd INSTANCE = new NBTTagEnd();

    private NBTTagEnd() {}

    @Override
    public void write(DataOutput dataoutput) throws IOException {}

    @Override
    public int sizeInBytes() {
        return 8;
    }

    @Override
    public byte getId() {
        return 0;
    }

    @Override
    public NBTTagType<NBTTagEnd> getType() {
        return NBTTagEnd.TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public NBTTagEnd copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitEnd(this);
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        return streamtagvisitor.visitEnd();
    }
}

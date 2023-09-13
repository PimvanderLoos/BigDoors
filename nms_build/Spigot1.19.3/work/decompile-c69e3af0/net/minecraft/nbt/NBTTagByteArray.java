package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagByteArray extends NBTList<NBTTagByte> {

    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final NBTTagType<NBTTagByteArray> TYPE = new NBTTagType.b<NBTTagByteArray>() {
        @Override
        public NBTTagByteArray load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(24L);
            int j = datainput.readInt();

            nbtreadlimiter.accountBytes(1L * (long) j);
            byte[] abyte = new byte[j];

            datainput.readFully(abyte);
            return new NBTTagByteArray(abyte);
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            int i = datainput.readInt();
            byte[] abyte = new byte[i];

            datainput.readFully(abyte);
            return streamtagvisitor.visit(abyte);
        }

        @Override
        public void skip(DataInput datainput) throws IOException {
            datainput.skipBytes(datainput.readInt() * 1);
        }

        @Override
        public String getName() {
            return "BYTE[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Byte_Array";
        }
    };
    private byte[] data;

    public NBTTagByteArray(byte[] abyte) {
        this.data = abyte;
    }

    public NBTTagByteArray(List<Byte> list) {
        this(toArray(list));
    }

    private static byte[] toArray(List<Byte> list) {
        byte[] abyte = new byte[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Byte obyte = (Byte) list.get(i);

            abyte[i] = obyte == null ? 0 : obyte;
        }

        return abyte;
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data.length);
        dataoutput.write(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 24 + 1 * this.data.length;
    }

    @Override
    public byte getId() {
        return 7;
    }

    @Override
    public NBTTagType<NBTTagByteArray> getType() {
        return NBTTagByteArray.TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public NBTBase copy() {
        byte[] abyte = new byte[this.data.length];

        System.arraycopy(this.data, 0, abyte, 0, this.data.length);
        return new NBTTagByteArray(abyte);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagByteArray && Arrays.equals(this.data, ((NBTTagByteArray) object).data);
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitByteArray(this);
    }

    public byte[] getAsByteArray() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public NBTTagByte get(int i) {
        return NBTTagByte.valueOf(this.data[i]);
    }

    public NBTTagByte set(int i, NBTTagByte nbttagbyte) {
        byte b0 = this.data[i];

        this.data[i] = nbttagbyte.getAsByte();
        return NBTTagByte.valueOf(b0);
    }

    public void add(int i, NBTTagByte nbttagbyte) {
        this.data = ArrayUtils.add(this.data, i, nbttagbyte.getAsByte());
    }

    @Override
    public boolean setTag(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data[i] = ((NBTNumber) nbtbase).getAsByte();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data = ArrayUtils.add(this.data, i, ((NBTNumber) nbtbase).getAsByte());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagByte remove(int i) {
        byte b0 = this.data[i];

        this.data = ArrayUtils.remove(this.data, i);
        return NBTTagByte.valueOf(b0);
    }

    @Override
    public byte getElementType() {
        return 1;
    }

    public void clear() {
        this.data = new byte[0];
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        return streamtagvisitor.visit(this.data);
    }
}

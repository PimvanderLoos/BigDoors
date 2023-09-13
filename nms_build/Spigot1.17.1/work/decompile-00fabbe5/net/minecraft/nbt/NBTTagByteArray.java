package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagByteArray extends NBTList<NBTTagByte> {

    private static final int SELF_SIZE_IN_BITS = 192;
    public static final NBTTagType<NBTTagByteArray> TYPE = new NBTTagType<NBTTagByteArray>() {
        @Override
        public NBTTagByteArray b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(192L);
            int j = datainput.readInt();

            nbtreadlimiter.a(8L * (long) j);
            byte[] abyte = new byte[j];

            datainput.readFully(abyte);
            return new NBTTagByteArray(abyte);
        }

        @Override
        public String a() {
            return "BYTE[]";
        }

        @Override
        public String b() {
            return "TAG_Byte_Array";
        }
    };
    private byte[] data;

    public NBTTagByteArray(byte[] abyte) {
        this.data = abyte;
    }

    public NBTTagByteArray(List<Byte> list) {
        this(a(list));
    }

    private static byte[] a(List<Byte> list) {
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
    public byte getTypeId() {
        return 7;
    }

    @Override
    public NBTTagType<NBTTagByteArray> b() {
        return NBTTagByteArray.TYPE;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public NBTBase clone() {
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
    public void a(TagVisitor tagvisitor) {
        tagvisitor.a(this);
    }

    public byte[] getBytes() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public NBTTagByte get(int i) {
        return NBTTagByte.a(this.data[i]);
    }

    public NBTTagByte set(int i, NBTTagByte nbttagbyte) {
        byte b0 = this.data[i];

        this.data[i] = nbttagbyte.asByte();
        return NBTTagByte.a(b0);
    }

    public void add(int i, NBTTagByte nbttagbyte) {
        this.data = ArrayUtils.add(this.data, i, nbttagbyte.asByte());
    }

    @Override
    public boolean a(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data[i] = ((NBTNumber) nbtbase).asByte();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean b(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data = ArrayUtils.add(this.data, i, ((NBTNumber) nbtbase).asByte());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagByte remove(int i) {
        byte b0 = this.data[i];

        this.data = ArrayUtils.remove(this.data, i);
        return NBTTagByte.a(b0);
    }

    @Override
    public byte e() {
        return 1;
    }

    public void clear() {
        this.data = new byte[0];
    }
}

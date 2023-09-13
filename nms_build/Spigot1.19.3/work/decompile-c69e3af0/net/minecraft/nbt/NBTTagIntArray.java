package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagIntArray extends NBTList<NBTTagInt> {

    private static final int SELF_SIZE_IN_BYTES = 24;
    public static final NBTTagType<NBTTagIntArray> TYPE = new NBTTagType.b<NBTTagIntArray>() {
        @Override
        public NBTTagIntArray load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.accountBytes(24L);
            int j = datainput.readInt();

            nbtreadlimiter.accountBytes(4L * (long) j);
            int[] aint = new int[j];

            for (int k = 0; k < j; ++k) {
                aint[k] = datainput.readInt();
            }

            return new NBTTagIntArray(aint);
        }

        @Override
        public StreamTagVisitor.b parse(DataInput datainput, StreamTagVisitor streamtagvisitor) throws IOException {
            int i = datainput.readInt();
            int[] aint = new int[i];

            for (int j = 0; j < i; ++j) {
                aint[j] = datainput.readInt();
            }

            return streamtagvisitor.visit(aint);
        }

        @Override
        public void skip(DataInput datainput) throws IOException {
            datainput.skipBytes(datainput.readInt() * 4);
        }

        @Override
        public String getName() {
            return "INT[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Int_Array";
        }
    };
    private int[] data;

    public NBTTagIntArray(int[] aint) {
        this.data = aint;
    }

    public NBTTagIntArray(List<Integer> list) {
        this(toArray(list));
    }

    private static int[] toArray(List<Integer> list) {
        int[] aint = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Integer integer = (Integer) list.get(i);

            aint[i] = integer == null ? 0 : integer;
        }

        return aint;
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data.length);
        int[] aint = this.data;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint[j];

            dataoutput.writeInt(k);
        }

    }

    @Override
    public int sizeInBytes() {
        return 24 + 4 * this.data.length;
    }

    @Override
    public byte getId() {
        return 11;
    }

    @Override
    public NBTTagType<NBTTagIntArray> getType() {
        return NBTTagIntArray.TYPE;
    }

    @Override
    public String toString() {
        return this.getAsString();
    }

    @Override
    public NBTTagIntArray copy() {
        int[] aint = new int[this.data.length];

        System.arraycopy(this.data, 0, aint, 0, this.data.length);
        return new NBTTagIntArray(aint);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagIntArray && Arrays.equals(this.data, ((NBTTagIntArray) object).data);
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public int[] getAsIntArray() {
        return this.data;
    }

    @Override
    public void accept(TagVisitor tagvisitor) {
        tagvisitor.visitIntArray(this);
    }

    public int size() {
        return this.data.length;
    }

    public NBTTagInt get(int i) {
        return NBTTagInt.valueOf(this.data[i]);
    }

    public NBTTagInt set(int i, NBTTagInt nbttagint) {
        int j = this.data[i];

        this.data[i] = nbttagint.getAsInt();
        return NBTTagInt.valueOf(j);
    }

    public void add(int i, NBTTagInt nbttagint) {
        this.data = ArrayUtils.add(this.data, i, nbttagint.getAsInt());
    }

    @Override
    public boolean setTag(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data[i] = ((NBTNumber) nbtbase).getAsInt();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data = ArrayUtils.add(this.data, i, ((NBTNumber) nbtbase).getAsInt());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagInt remove(int i) {
        int j = this.data[i];

        this.data = ArrayUtils.remove(this.data, i);
        return NBTTagInt.valueOf(j);
    }

    @Override
    public byte getElementType() {
        return 3;
    }

    public void clear() {
        this.data = new int[0];
    }

    @Override
    public StreamTagVisitor.b accept(StreamTagVisitor streamtagvisitor) {
        return streamtagvisitor.visit(this.data);
    }
}

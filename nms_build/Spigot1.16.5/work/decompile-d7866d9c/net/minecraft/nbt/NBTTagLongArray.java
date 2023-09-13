package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagLongArray extends NBTList<NBTTagLong> {

    public static final NBTTagType<NBTTagLongArray> a = new NBTTagType<NBTTagLongArray>() {
        @Override
        public NBTTagLongArray b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(192L);
            int j = datainput.readInt();

            nbtreadlimiter.a(64L * (long) j);
            long[] along = new long[j];

            for (int k = 0; k < j; ++k) {
                along[k] = datainput.readLong();
            }

            return new NBTTagLongArray(along);
        }

        @Override
        public String a() {
            return "LONG[]";
        }

        @Override
        public String b() {
            return "TAG_Long_Array";
        }
    };
    private long[] b;

    public NBTTagLongArray(long[] along) {
        this.b = along;
    }

    public NBTTagLongArray(LongSet longset) {
        this.b = longset.toLongArray();
    }

    public NBTTagLongArray(List<Long> list) {
        this(a(list));
    }

    private static long[] a(List<Long> list) {
        long[] along = new long[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Long olong = (Long) list.get(i);

            along[i] = olong == null ? 0L : olong;
        }

        return along;
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.b.length);
        long[] along = this.b;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along[j];

            dataoutput.writeLong(k);
        }

    }

    @Override
    public byte getTypeId() {
        return 12;
    }

    @Override
    public NBTTagType<NBTTagLongArray> b() {
        return NBTTagLongArray.a;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[L;");

        for (int i = 0; i < this.b.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.b[i]).append('L');
        }

        return stringbuilder.append(']').toString();
    }

    @Override
    public NBTTagLongArray clone() {
        long[] along = new long[this.b.length];

        System.arraycopy(this.b, 0, along, 0, this.b.length);
        return new NBTTagLongArray(along);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLongArray && Arrays.equals(this.b, ((NBTTagLongArray) object).b);
    }

    public int hashCode() {
        return Arrays.hashCode(this.b);
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("L")).a(NBTTagLongArray.g);
        IChatMutableComponent ichatmutablecomponent1 = (new ChatComponentText("[")).addSibling(ichatmutablecomponent).c(";");

        for (int j = 0; j < this.b.length; ++j) {
            IChatMutableComponent ichatmutablecomponent2 = (new ChatComponentText(String.valueOf(this.b[j]))).a(NBTTagLongArray.f);

            ichatmutablecomponent1.c(" ").addSibling(ichatmutablecomponent2).addSibling(ichatmutablecomponent);
            if (j != this.b.length - 1) {
                ichatmutablecomponent1.c(",");
            }
        }

        ichatmutablecomponent1.c("]");
        return ichatmutablecomponent1;
    }

    public long[] getLongs() {
        return this.b;
    }

    public int size() {
        return this.b.length;
    }

    public NBTTagLong get(int i) {
        return NBTTagLong.a(this.b[i]);
    }

    public NBTTagLong set(int i, NBTTagLong nbttaglong) {
        long j = this.b[i];

        this.b[i] = nbttaglong.asLong();
        return NBTTagLong.a(j);
    }

    public void add(int i, NBTTagLong nbttaglong) {
        this.b = ArrayUtils.add(this.b, i, nbttaglong.asLong());
    }

    @Override
    public boolean a(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.b[i] = ((NBTNumber) nbtbase).asLong();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean b(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.b = ArrayUtils.add(this.b, i, ((NBTNumber) nbtbase).asLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagLong remove(int i) {
        long j = this.b[i];

        this.b = ArrayUtils.remove(this.b, i);
        return NBTTagLong.a(j);
    }

    @Override
    public byte d_() {
        return 4;
    }

    public void clear() {
        this.b = new long[0];
    }
}

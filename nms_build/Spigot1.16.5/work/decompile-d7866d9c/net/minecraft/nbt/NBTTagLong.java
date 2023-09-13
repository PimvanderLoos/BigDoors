package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class NBTTagLong extends NBTNumber {

    public static final NBTTagType<NBTTagLong> a = new NBTTagType<NBTTagLong>() {
        @Override
        public NBTTagLong b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(128L);
            return NBTTagLong.a(datainput.readLong());
        }

        @Override
        public String a() {
            return "LONG";
        }

        @Override
        public String b() {
            return "TAG_Long";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final long data;

    private NBTTagLong(long i) {
        this.data = i;
    }

    public static NBTTagLong a(long i) {
        return i >= -128L && i <= 1024L ? NBTTagLong.a.a[(int) i + 128] : new NBTTagLong(i);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeLong(this.data);
    }

    @Override
    public byte getTypeId() {
        return 4;
    }

    @Override
    public NBTTagType<NBTTagLong> b() {
        return NBTTagLong.a;
    }

    @Override
    public String toString() {
        return this.data + "L";
    }

    @Override
    public NBTTagLong clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagLong && this.data == ((NBTTagLong) object).data;
    }

    public int hashCode() {
        return (int) (this.data ^ this.data >>> 32);
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("L")).a(NBTTagLong.g);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatmutablecomponent).a(NBTTagLong.f);
    }

    @Override
    public long asLong() {
        return this.data;
    }

    @Override
    public int asInt() {
        return (int) (this.data & -1L);
    }

    @Override
    public short asShort() {
        return (short) ((int) (this.data & 65535L));
    }

    @Override
    public byte asByte() {
        return (byte) ((int) (this.data & 255L));
    }

    @Override
    public double asDouble() {
        return (double) this.data;
    }

    @Override
    public float asFloat() {
        return (float) this.data;
    }

    @Override
    public Number k() {
        return this.data;
    }

    static class a {

        static final NBTTagLong[] a = new NBTTagLong[1153];

        static {
            for (int i = 0; i < NBTTagLong.a.a.length; ++i) {
                NBTTagLong.a.a[i] = new NBTTagLong((long) (-128 + i));
            }

        }
    }
}

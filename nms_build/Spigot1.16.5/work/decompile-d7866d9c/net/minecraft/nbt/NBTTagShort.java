package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class NBTTagShort extends NBTNumber {

    public static final NBTTagType<NBTTagShort> a = new NBTTagType<NBTTagShort>() {
        @Override
        public NBTTagShort b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(80L);
            return NBTTagShort.a(datainput.readShort());
        }

        @Override
        public String a() {
            return "SHORT";
        }

        @Override
        public String b() {
            return "TAG_Short";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final short data;

    private NBTTagShort(short short0) {
        this.data = short0;
    }

    public static NBTTagShort a(short short0) {
        return short0 >= -128 && short0 <= 1024 ? NBTTagShort.a.a[short0 + 128] : new NBTTagShort(short0);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeShort(this.data);
    }

    @Override
    public byte getTypeId() {
        return 2;
    }

    @Override
    public NBTTagType<NBTTagShort> b() {
        return NBTTagShort.a;
    }

    @Override
    public String toString() {
        return this.data + "s";
    }

    @Override
    public NBTTagShort clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagShort && this.data == ((NBTTagShort) object).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("s")).a(NBTTagShort.g);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatmutablecomponent).a(NBTTagShort.f);
    }

    @Override
    public long asLong() {
        return (long) this.data;
    }

    @Override
    public int asInt() {
        return this.data;
    }

    @Override
    public short asShort() {
        return this.data;
    }

    @Override
    public byte asByte() {
        return (byte) (this.data & 255);
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

        static final NBTTagShort[] a = new NBTTagShort[1153];

        static {
            for (int i = 0; i < NBTTagShort.a.a.length; ++i) {
                NBTTagShort.a.a[i] = new NBTTagShort((short) (-128 + i));
            }

        }
    }
}

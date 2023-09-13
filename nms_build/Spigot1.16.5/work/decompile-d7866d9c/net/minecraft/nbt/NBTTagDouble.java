package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTNumber {

    public static final NBTTagDouble a = new NBTTagDouble(0.0D);
    public static final NBTTagType<NBTTagDouble> b = new NBTTagType<NBTTagDouble>() {
        @Override
        public NBTTagDouble b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(128L);
            return NBTTagDouble.a(datainput.readDouble());
        }

        @Override
        public String a() {
            return "DOUBLE";
        }

        @Override
        public String b() {
            return "TAG_Double";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final double data;

    private NBTTagDouble(double d0) {
        this.data = d0;
    }

    public static NBTTagDouble a(double d0) {
        return d0 == 0.0D ? NBTTagDouble.a : new NBTTagDouble(d0);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeDouble(this.data);
    }

    @Override
    public byte getTypeId() {
        return 6;
    }

    @Override
    public NBTTagType<NBTTagDouble> b() {
        return NBTTagDouble.b;
    }

    @Override
    public String toString() {
        return this.data + "d";
    }

    @Override
    public NBTTagDouble clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagDouble && this.data == ((NBTTagDouble) object).data;
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.data);

        return (int) (i ^ i >>> 32);
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("d")).a(NBTTagDouble.g);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatmutablecomponent).a(NBTTagDouble.f);
    }

    @Override
    public long asLong() {
        return (long) Math.floor(this.data);
    }

    @Override
    public int asInt() {
        return MathHelper.floor(this.data);
    }

    @Override
    public short asShort() {
        return (short) (MathHelper.floor(this.data) & '\uffff');
    }

    @Override
    public byte asByte() {
        return (byte) (MathHelper.floor(this.data) & 255);
    }

    @Override
    public double asDouble() {
        return this.data;
    }

    @Override
    public float asFloat() {
        return (float) this.data;
    }

    @Override
    public Number k() {
        return this.data;
    }
}

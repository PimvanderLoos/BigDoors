package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTNumber {

    public static final NBTTagFloat a = new NBTTagFloat(0.0F);
    public static final NBTTagType<NBTTagFloat> b = new NBTTagType<NBTTagFloat>() {
        @Override
        public NBTTagFloat b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
            nbtreadlimiter.a(96L);
            return NBTTagFloat.a(datainput.readFloat());
        }

        @Override
        public String a() {
            return "FLOAT";
        }

        @Override
        public String b() {
            return "TAG_Float";
        }

        @Override
        public boolean c() {
            return true;
        }
    };
    private final float data;

    private NBTTagFloat(float f) {
        this.data = f;
    }

    public static NBTTagFloat a(float f) {
        return f == 0.0F ? NBTTagFloat.a : new NBTTagFloat(f);
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeFloat(this.data);
    }

    @Override
    public byte getTypeId() {
        return 5;
    }

    @Override
    public NBTTagType<NBTTagFloat> b() {
        return NBTTagFloat.b;
    }

    @Override
    public String toString() {
        return this.data + "f";
    }

    @Override
    public NBTTagFloat clone() {
        return this;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagFloat && this.data == ((NBTTagFloat) object).data;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.data);
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("f")).a(NBTTagFloat.g);

        return (new ChatComponentText(String.valueOf(this.data))).addSibling(ichatmutablecomponent).a(NBTTagFloat.f);
    }

    @Override
    public long asLong() {
        return (long) this.data;
    }

    @Override
    public int asInt() {
        return MathHelper.d(this.data);
    }

    @Override
    public short asShort() {
        return (short) (MathHelper.d(this.data) & '\uffff');
    }

    @Override
    public byte asByte() {
        return (byte) (MathHelper.d(this.data) & 255);
    }

    @Override
    public double asDouble() {
        return (double) this.data;
    }

    @Override
    public float asFloat() {
        return this.data;
    }

    @Override
    public Number k() {
        return this.data;
    }
}

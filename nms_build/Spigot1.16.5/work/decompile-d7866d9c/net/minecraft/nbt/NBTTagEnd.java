package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;

public class NBTTagEnd implements NBTBase {

    public static final NBTTagType<NBTTagEnd> a = new NBTTagType<NBTTagEnd>() {
        @Override
        public NBTTagEnd b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
            nbtreadlimiter.a(64L);
            return NBTTagEnd.b;
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
    public static final NBTTagEnd b = new NBTTagEnd();

    private NBTTagEnd() {}

    @Override
    public void write(DataOutput dataoutput) throws IOException {}

    @Override
    public byte getTypeId() {
        return 0;
    }

    @Override
    public NBTTagType<NBTTagEnd> b() {
        return NBTTagEnd.a;
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public NBTTagEnd clone() {
        return this;
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        return ChatComponentText.d;
    }
}

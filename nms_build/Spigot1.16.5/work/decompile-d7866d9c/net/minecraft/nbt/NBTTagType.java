package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface NBTTagType<T extends NBTBase> {

    T b(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException;

    default boolean c() {
        return false;
    }

    String a();

    String b();

    static NBTTagType<NBTTagEnd> a(final int i) {
        return new NBTTagType<NBTTagEnd>() {
            @Override
            public NBTTagEnd b(DataInput datainput, int j, NBTReadLimiter nbtreadlimiter) throws IOException {
                throw new IllegalArgumentException("Invalid tag id: " + i);
            }

            @Override
            public String a() {
                return "INVALID[" + i + "]";
            }

            @Override
            public String b() {
                return "UNKNOWN_" + i;
            }
        };
    }
}

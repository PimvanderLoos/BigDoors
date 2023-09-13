package net.minecraft.util;

import java.util.function.IntConsumer;

public interface DataBits {

    int getAndSet(int i, int j);

    void set(int i, int j);

    int get(int i);

    long[] getRaw();

    int getSize();

    int getBits();

    void getAll(IntConsumer intconsumer);

    void unpack(int[] aint);

    DataBits copy();
}

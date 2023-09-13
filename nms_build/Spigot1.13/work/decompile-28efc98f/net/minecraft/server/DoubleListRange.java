package net.minecraft.server;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class DoubleListRange extends AbstractDoubleList {

    private final int a;
    private final int b;

    DoubleListRange(int i, int j) {
        this.a = i;
        this.b = j;
    }

    public double getDouble(int i) {
        return (double) (this.b + i);
    }

    public int size() {
        return this.a + 1;
    }
}

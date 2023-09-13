package net.minecraft.core;

public final class QuartPos {

    public static final int BITS = 2;
    public static final int SIZE = 4;
    private static final int SECTION_TO_QUARTS_BITS = 2;

    private QuartPos() {}

    public static int a(int i) {
        return i >> 2;
    }

    public static int b(int i) {
        return i << 2;
    }

    public static int c(int i) {
        return i << 2;
    }

    public static int d(int i) {
        return i >> 2;
    }
}

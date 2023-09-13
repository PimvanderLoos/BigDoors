package net.minecraft.util;

public class LinearCongruentialGenerator {

    public static long a(long i, long j) {
        i *= i * 6364136223846793005L + 1442695040888963407L;
        i += j;
        return i;
    }
}

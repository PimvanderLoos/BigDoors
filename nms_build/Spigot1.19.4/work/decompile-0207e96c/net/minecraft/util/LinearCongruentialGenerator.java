package net.minecraft.util;

public class LinearCongruentialGenerator {

    private static final long MULTIPLIER = 6364136223846793005L;
    private static final long INCREMENT = 1442695040888963407L;

    public LinearCongruentialGenerator() {}

    public static long next(long i, long j) {
        i *= i * 6364136223846793005L + 1442695040888963407L;
        i += j;
        return i;
    }
}

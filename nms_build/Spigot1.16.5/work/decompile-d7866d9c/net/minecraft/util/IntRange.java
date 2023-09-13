package net.minecraft.util;

import java.util.Random;

public class IntRange {

    private final int a;
    private final int b;

    public IntRange(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + i + ", Given max: " + j);
        } else {
            this.a = i;
            this.b = j;
        }
    }

    public static IntRange a(int i, int j) {
        return new IntRange(i, j);
    }

    public int a(Random random) {
        return this.a == this.b ? this.a : random.nextInt(this.b - this.a + 1) + this.a;
    }

    public int a() {
        return this.a;
    }

    public int b() {
        return this.b;
    }

    public String toString() {
        return "IntRange[" + this.a + "-" + this.b + "]";
    }
}

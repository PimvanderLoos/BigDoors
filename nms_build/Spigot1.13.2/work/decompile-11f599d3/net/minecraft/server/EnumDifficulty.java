package net.minecraft.server;

import java.util.Arrays;
import java.util.Comparator;

public enum EnumDifficulty {

    PEACEFUL(0, "peaceful"), EASY(1, "easy"), NORMAL(2, "normal"), HARD(3, "hard");

    private static final EnumDifficulty[] e = (EnumDifficulty[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EnumDifficulty::a)).toArray((i) -> {
        return new EnumDifficulty[i];
    });
    private final int f;
    private final String g;

    private EnumDifficulty(int i, String s) {
        this.f = i;
        this.g = s;
    }

    public int a() {
        return this.f;
    }

    public IChatBaseComponent b() {
        return new ChatMessage("options.difficulty." + this.g, new Object[0]);
    }

    public static EnumDifficulty getById(int i) {
        return EnumDifficulty.e[i % EnumDifficulty.e.length];
    }

    public String c() {
        return this.g;
    }
}

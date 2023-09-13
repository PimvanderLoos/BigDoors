package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseStyle {

    NONE(0), WHITE(1), WHITE_FIELD(2), WHITE_DOTS(3), BLACK_DOTS(4);

    private static final HorseStyle[] f = (HorseStyle[]) Arrays.stream(values()).sorted(Comparator.comparingInt(HorseStyle::a)).toArray((i) -> {
        return new HorseStyle[i];
    });
    private final int g;

    private HorseStyle(int i) {
        this.g = i;
    }

    public int a() {
        return this.g;
    }

    public static HorseStyle a(int i) {
        return HorseStyle.f[i % HorseStyle.f.length];
    }
}

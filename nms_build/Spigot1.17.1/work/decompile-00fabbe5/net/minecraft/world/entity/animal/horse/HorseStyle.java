package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseStyle {

    NONE(0), WHITE(1), WHITE_FIELD(2), WHITE_DOTS(3), BLACK_DOTS(4);

    private static final HorseStyle[] BY_ID = (HorseStyle[]) Arrays.stream(values()).sorted(Comparator.comparingInt(HorseStyle::a)).toArray((i) -> {
        return new HorseStyle[i];
    });
    private final int id;

    private HorseStyle(int i) {
        this.id = i;
    }

    public int a() {
        return this.id;
    }

    public static HorseStyle a(int i) {
        return HorseStyle.BY_ID[i % HorseStyle.BY_ID.length];
    }
}

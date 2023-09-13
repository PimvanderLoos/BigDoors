package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseColor {

    WHITE(0), CREAMY(1), CHESTNUT(2), BROWN(3), BLACK(4), GRAY(5), DARKBROWN(6);

    private static final HorseColor[] h = (HorseColor[]) Arrays.stream(values()).sorted(Comparator.comparingInt(HorseColor::a)).toArray((i) -> {
        return new HorseColor[i];
    });
    private final int i;

    private HorseColor(int i) {
        this.i = i;
    }

    public int a() {
        return this.i;
    }

    public static HorseColor a(int i) {
        return HorseColor.h[i % HorseColor.h.length];
    }
}

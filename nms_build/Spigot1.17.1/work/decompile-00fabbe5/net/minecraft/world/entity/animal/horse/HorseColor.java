package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseColor {

    WHITE(0), CREAMY(1), CHESTNUT(2), BROWN(3), BLACK(4), GRAY(5), DARKBROWN(6);

    private static final HorseColor[] BY_ID = (HorseColor[]) Arrays.stream(values()).sorted(Comparator.comparingInt(HorseColor::a)).toArray((i) -> {
        return new HorseColor[i];
    });
    private final int id;

    private HorseColor(int i) {
        this.id = i;
    }

    public int a() {
        return this.id;
    }

    public static HorseColor a(int i) {
        return HorseColor.BY_ID[i % HorseColor.BY_ID.length];
    }
}

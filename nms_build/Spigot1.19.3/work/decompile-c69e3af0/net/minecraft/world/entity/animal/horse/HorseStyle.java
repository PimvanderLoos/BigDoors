package net.minecraft.world.entity.animal.horse;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;

public enum HorseStyle {

    NONE(0), WHITE(1), WHITE_FIELD(2), WHITE_DOTS(3), BLACK_DOTS(4);

    private static final IntFunction<HorseStyle> BY_ID = ByIdMap.continuous(HorseStyle::getId, values(), ByIdMap.a.WRAP);
    private final int id;

    private HorseStyle(int i) {
        this.id = i;
    }

    public int getId() {
        return this.id;
    }

    public static HorseStyle byId(int i) {
        return (HorseStyle) HorseStyle.BY_ID.apply(i);
    }
}

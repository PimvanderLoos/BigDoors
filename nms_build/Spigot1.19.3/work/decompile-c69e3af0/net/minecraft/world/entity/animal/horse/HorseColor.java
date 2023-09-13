package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.INamable;

public enum HorseColor implements INamable {

    WHITE(0, "white"), CREAMY(1, "creamy"), CHESTNUT(2, "chestnut"), BROWN(3, "brown"), BLACK(4, "black"), GRAY(5, "gray"), DARK_BROWN(6, "dark_brown");

    public static final Codec<HorseColor> CODEC = INamable.fromEnum(HorseColor::values);
    private static final IntFunction<HorseColor> BY_ID = ByIdMap.continuous(HorseColor::getId, values(), ByIdMap.a.WRAP);
    private final int id;
    private final String name;

    private HorseColor(int i, String s) {
        this.id = i;
        this.name = s;
    }

    public int getId() {
        return this.id;
    }

    public static HorseColor byId(int i) {
        return (HorseColor) HorseColor.BY_ID.apply(i);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}

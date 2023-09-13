package net.minecraft.world;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.INamable;

public enum EnumDifficulty implements INamable {

    PEACEFUL(0, "peaceful"), EASY(1, "easy"), NORMAL(2, "normal"), HARD(3, "hard");

    public static final INamable.a<EnumDifficulty> CODEC = INamable.fromEnum(EnumDifficulty::values);
    private static final IntFunction<EnumDifficulty> BY_ID = ByIdMap.continuous(EnumDifficulty::getId, values(), ByIdMap.a.WRAP);
    private final int id;
    private final String key;

    private EnumDifficulty(int i, String s) {
        this.id = i;
        this.key = s;
    }

    public int getId() {
        return this.id;
    }

    public IChatBaseComponent getDisplayName() {
        return IChatBaseComponent.translatable("options.difficulty." + this.key);
    }

    public IChatBaseComponent getInfo() {
        return IChatBaseComponent.translatable("options.difficulty." + this.key + ".info");
    }

    public static EnumDifficulty byId(int i) {
        return (EnumDifficulty) EnumDifficulty.BY_ID.apply(i);
    }

    @Nullable
    public static EnumDifficulty byName(String s) {
        return (EnumDifficulty) EnumDifficulty.CODEC.byName(s);
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String getSerializedName() {
        return this.key;
    }
}

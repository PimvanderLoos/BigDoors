package net.minecraft.world.item.crafting;

import net.minecraft.util.INamable;

public enum CookingBookCategory implements INamable {

    FOOD("food"), BLOCKS("blocks"), MISC("misc");

    public static final INamable.a<CookingBookCategory> CODEC = INamable.fromEnum(CookingBookCategory::values);
    private final String name;

    private CookingBookCategory(String s) {
        this.name = s;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}

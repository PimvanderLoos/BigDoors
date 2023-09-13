package net.minecraft.world.item.crafting;

import net.minecraft.util.INamable;

public enum CraftingBookCategory implements INamable {

    BUILDING("building"), REDSTONE("redstone"), EQUIPMENT("equipment"), MISC("misc");

    public static final INamable.a<CraftingBookCategory> CODEC = INamable.fromEnum(CraftingBookCategory::values);
    private final String name;

    private CraftingBookCategory(String s) {
        this.name = s;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}

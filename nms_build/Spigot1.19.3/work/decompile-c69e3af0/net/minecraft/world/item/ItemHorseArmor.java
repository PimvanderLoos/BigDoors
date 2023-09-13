package net.minecraft.world.item;

import net.minecraft.resources.MinecraftKey;

public class ItemHorseArmor extends Item {

    private static final String TEX_FOLDER = "textures/entity/horse/";
    private final int protection;
    private final String texture;

    public ItemHorseArmor(int i, String s, Item.Info item_info) {
        super(item_info);
        this.protection = i;
        this.texture = "textures/entity/horse/armor/horse_armor_" + s + ".png";
    }

    public MinecraftKey getTexture() {
        return new MinecraftKey(this.texture);
    }

    public int getProtection() {
        return this.protection;
    }
}

package net.minecraft.world.item;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.crafting.RecipeItemStack;

public interface ArmorMaterial {

    int getDurabilityForSlot(EnumItemSlot enumitemslot);

    int getDefenseForSlot(EnumItemSlot enumitemslot);

    int getEnchantmentValue();

    SoundEffect getEquipSound();

    RecipeItemStack getRepairIngredient();

    String getName();

    float getToughness();

    float getKnockbackResistance();
}

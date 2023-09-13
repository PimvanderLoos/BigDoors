package net.minecraft.world.item;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.item.crafting.RecipeItemStack;

public interface ArmorMaterial {

    int getDurabilityForType(ItemArmor.a itemarmor_a);

    int getDefenseForType(ItemArmor.a itemarmor_a);

    int getEnchantmentValue();

    SoundEffect getEquipSound();

    RecipeItemStack getRepairIngredient();

    String getName();

    float getToughness();

    float getKnockbackResistance();
}

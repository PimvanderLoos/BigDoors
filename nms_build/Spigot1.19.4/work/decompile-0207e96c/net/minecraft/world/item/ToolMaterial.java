package net.minecraft.world.item;

import net.minecraft.world.item.crafting.RecipeItemStack;

public interface ToolMaterial {

    int getUses();

    float getSpeed();

    float getAttackDamageBonus();

    int getLevel();

    int getEnchantmentValue();

    RecipeItemStack getRepairIngredient();
}

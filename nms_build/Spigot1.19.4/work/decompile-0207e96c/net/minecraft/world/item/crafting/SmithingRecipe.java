package net.minecraft.world.item.crafting;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public interface SmithingRecipe extends IRecipe<IInventory> {

    @Override
    default Recipes<?> getType() {
        return Recipes.SMITHING;
    }

    @Override
    default boolean canCraftInDimensions(int i, int j) {
        return i >= 3 && j >= 1;
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    boolean isTemplateIngredient(ItemStack itemstack);

    boolean isBaseIngredient(ItemStack itemstack);

    boolean isAdditionIngredient(ItemStack itemstack);
}

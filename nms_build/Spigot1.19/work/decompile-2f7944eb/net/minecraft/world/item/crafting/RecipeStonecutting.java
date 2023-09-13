package net.minecraft.world.item.crafting;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public class RecipeStonecutting extends RecipeSingleItem {

    public RecipeStonecutting(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack) {
        super(Recipes.STONECUTTING, RecipeSerializer.STONECUTTER, minecraftkey, s, recipeitemstack, itemstack);
    }

    @Override
    public boolean matches(IInventory iinventory, World world) {
        return this.ingredient.test(iinventory.getItem(0));
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.STONECUTTER);
    }
}

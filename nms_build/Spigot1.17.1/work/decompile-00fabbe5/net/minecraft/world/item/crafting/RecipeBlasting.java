package net.minecraft.world.item.crafting;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class RecipeBlasting extends RecipeCooking {

    public RecipeBlasting(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i) {
        super(Recipes.BLASTING, minecraftkey, s, recipeitemstack, itemstack, f, i);
    }

    @Override
    public ItemStack h() {
        return new ItemStack(Blocks.BLAST_FURNACE);
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.BLASTING_RECIPE;
    }
}

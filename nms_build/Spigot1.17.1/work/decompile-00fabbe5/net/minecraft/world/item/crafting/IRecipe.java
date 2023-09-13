package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public interface IRecipe<C extends IInventory> {

    boolean a(C c0, World world);

    ItemStack a(C c0);

    boolean a(int i, int j);

    ItemStack getResult();

    default NonNullList<ItemStack> b(C c0) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(c0.getSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            Item item = c0.getItem(i).getItem();

            if (item.s()) {
                nonnulllist.set(i, new ItemStack(item.getCraftingRemainingItem()));
            }
        }

        return nonnulllist;
    }

    default NonNullList<RecipeItemStack> a() {
        return NonNullList.a();
    }

    default boolean isComplex() {
        return false;
    }

    default String d() {
        return "";
    }

    default ItemStack h() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    MinecraftKey getKey();

    RecipeSerializer<?> getRecipeSerializer();

    Recipes<?> g();

    default boolean i() {
        NonNullList<RecipeItemStack> nonnulllist = this.a();

        return nonnulllist.isEmpty() || nonnulllist.stream().anyMatch((recipeitemstack) -> {
            return recipeitemstack.a().length == 0;
        });
    }
}

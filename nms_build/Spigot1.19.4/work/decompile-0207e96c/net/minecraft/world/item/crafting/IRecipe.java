package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public interface IRecipe<C extends IInventory> {

    boolean matches(C c0, World world);

    ItemStack assemble(C c0, IRegistryCustom iregistrycustom);

    boolean canCraftInDimensions(int i, int j);

    ItemStack getResultItem(IRegistryCustom iregistrycustom);

    default NonNullList<ItemStack> getRemainingItems(C c0) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(c0.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            Item item = c0.getItem(i).getItem();

            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, new ItemStack(item.getCraftingRemainingItem()));
            }
        }

        return nonnulllist;
    }

    default NonNullList<RecipeItemStack> getIngredients() {
        return NonNullList.create();
    }

    default boolean isSpecial() {
        return false;
    }

    default boolean showNotification() {
        return true;
    }

    default String getGroup() {
        return "";
    }

    default ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    MinecraftKey getId();

    RecipeSerializer<?> getSerializer();

    Recipes<?> getType();

    default boolean isIncomplete() {
        NonNullList<RecipeItemStack> nonnulllist = this.getIngredients();

        return nonnulllist.isEmpty() || nonnulllist.stream().anyMatch((recipeitemstack) -> {
            return recipeitemstack.getItems().length == 0;
        });
    }
}

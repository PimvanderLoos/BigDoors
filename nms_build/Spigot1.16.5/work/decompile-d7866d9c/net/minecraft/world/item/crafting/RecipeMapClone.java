package net.minecraft.world.item.crafting;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class RecipeMapClone extends IRecipeComplex {

    public RecipeMapClone(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = ItemStack.b;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemstack.isEmpty() && i > 0;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = ItemStack.b;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.b;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return ItemStack.b;
                    }

                    ++i;
                }
            }
        }

        if (!itemstack.isEmpty() && i >= 1) {
            ItemStack itemstack2 = itemstack.cloneItemStack();

            itemstack2.setCount(i + 1);
            return itemstack2;
        } else {
            return ItemStack.b;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.e;
    }
}

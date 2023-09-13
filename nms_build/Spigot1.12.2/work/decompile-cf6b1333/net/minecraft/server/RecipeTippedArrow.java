package net.minecraft.server;

import java.util.Collection;

public class RecipeTippedArrow implements IRecipe {

    public RecipeTippedArrow() {}

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        if (inventorycrafting.j() == 3 && inventorycrafting.i() == 3) {
            for (int i = 0; i < inventorycrafting.j(); ++i) {
                for (int j = 0; j < inventorycrafting.i(); ++j) {
                    ItemStack itemstack = inventorycrafting.c(i, j);

                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    Item item = itemstack.getItem();

                    if (i == 1 && j == 1) {
                        if (item != Items.LINGERING_POTION) {
                            return false;
                        }
                    } else if (item != Items.ARROW) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = inventorycrafting.c(1, 1);

        if (itemstack.getItem() != Items.LINGERING_POTION) {
            return ItemStack.a;
        } else {
            ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);

            PotionUtil.a(itemstack1, PotionUtil.d(itemstack));
            PotionUtil.a(itemstack1, (Collection) PotionUtil.b(itemstack));
            return itemstack1;
        }
    }

    public ItemStack b() {
        return ItemStack.a;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        return NonNullList.a(inventorycrafting.getSize(), ItemStack.a);
    }

    public boolean c() {
        return true;
    }
}

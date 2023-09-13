package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessRecipes implements IRecipe {

    private final ItemStack result;
    private final List<ItemStack> ingredients;

    public ShapelessRecipes(ItemStack itemstack, List<ItemStack> list) {
        this.result = itemstack;
        this.ingredients = list;
    }

    public ItemStack b() {
        return this.result;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack.getItem().s()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().r()));
            }
        }

        return nonnulllist;
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        ArrayList arraylist = Lists.newArrayList(this.ingredients);

        for (int i = 0; i < inventorycrafting.i(); ++i) {
            for (int j = 0; j < inventorycrafting.j(); ++j) {
                ItemStack itemstack = inventorycrafting.c(j, i);

                if (!itemstack.isEmpty()) {
                    boolean flag = false;
                    Iterator iterator = arraylist.iterator();

                    while (iterator.hasNext()) {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getData() == 32767 || itemstack.getData() == itemstack1.getData())) {
                            flag = true;
                            arraylist.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return arraylist.isEmpty();
    }

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        return this.result.cloneItemStack();
    }

    public int a() {
        return this.ingredients.size();
    }
}

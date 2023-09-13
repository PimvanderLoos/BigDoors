package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWrittenBook;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class RecipeBookClone extends IRecipeComplex {

    public RecipeBookClone(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = ItemStack.b;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemstack.isEmpty() && itemstack.hasTag() && i > 0;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = ItemStack.b;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.b;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return ItemStack.b;
                    }

                    ++i;
                }
            }
        }

        if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && ItemWrittenBook.d(itemstack) < 2) {
            ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
            NBTTagCompound nbttagcompound = itemstack.getTag().clone();

            nbttagcompound.setInt("generation", ItemWrittenBook.d(itemstack) + 1);
            itemstack2.setTag(nbttagcompound);
            return itemstack2;
        } else {
            return ItemStack.b;
        }
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.b);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack.getItem().p()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
            } else if (itemstack.getItem() instanceof ItemWrittenBook) {
                ItemStack itemstack1 = itemstack.cloneItemStack();

                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.d;
    }
}

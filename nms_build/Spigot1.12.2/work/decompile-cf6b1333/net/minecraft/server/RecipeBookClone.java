package net.minecraft.server;

public class RecipeBookClone implements IRecipe {

    public RecipeBookClone() {}

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

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

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.a;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return ItemStack.a;
                    }

                    ++i;
                }
            }
        }

        if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && ItemWrittenBook.h(itemstack) < 2) {
            ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);

            itemstack2.setTag(itemstack.getTag().g());
            itemstack2.getTag().setInt("generation", ItemWrittenBook.h(itemstack) + 1);
            if (itemstack.hasName()) {
                itemstack2.g(itemstack.getName());
            }

            return itemstack2;
        } else {
            return ItemStack.a;
        }
    }

    public ItemStack b() {
        return ItemStack.a;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack.getItem() instanceof ItemWrittenBook) {
                ItemStack itemstack1 = itemstack.cloneItemStack();

                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    public boolean c() {
        return true;
    }
}

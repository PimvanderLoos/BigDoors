package net.minecraft.server;

public class RecipiesShield {

    public RecipiesShield() {}

    public void a(CraftingManager craftingmanager) {
        craftingmanager.registerShapedRecipe(new ItemStack(Items.SHIELD), new Object[] { "WoW", "WWW", " W ", Character.valueOf('W'), Blocks.PLANKS, Character.valueOf('o'), Items.IRON_INGOT});
        craftingmanager.a(new RecipiesShield.Decoration(null));
    }

    static class Decoration implements IRecipe {

        private Decoration() {}

        public boolean a(InventoryCrafting inventorycrafting, World world) {
            ItemStack itemstack = ItemStack.a;
            ItemStack itemstack1 = ItemStack.a;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack2 = inventorycrafting.getItem(i);

                if (!itemstack2.isEmpty()) {
                    if (itemstack2.getItem() == Items.BANNER) {
                        if (!itemstack1.isEmpty()) {
                            return false;
                        }

                        itemstack1 = itemstack2;
                    } else {
                        if (itemstack2.getItem() != Items.SHIELD) {
                            return false;
                        }

                        if (!itemstack.isEmpty()) {
                            return false;
                        }

                        if (itemstack2.d("BlockEntityTag") != null) {
                            return false;
                        }

                        itemstack = itemstack2;
                    }
                }
            }

            if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        public ItemStack craftItem(InventoryCrafting inventorycrafting) {
            ItemStack itemstack = ItemStack.a;
            ItemStack itemstack1 = ItemStack.a;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack2 = inventorycrafting.getItem(i);

                if (!itemstack2.isEmpty()) {
                    if (itemstack2.getItem() == Items.BANNER) {
                        itemstack = itemstack2;
                    } else if (itemstack2.getItem() == Items.SHIELD) {
                        itemstack1 = itemstack2.cloneItemStack();
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                return itemstack1;
            } else {
                NBTTagCompound nbttagcompound = itemstack.d("BlockEntityTag");
                NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.g();

                nbttagcompound1.setInt("Base", itemstack.getData() & 15);
                itemstack1.a("BlockEntityTag", (NBTBase) nbttagcompound1);
                return itemstack1;
            }
        }

        public int a() {
            return 2;
        }

        public ItemStack b() {
            return ItemStack.a;
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

        Decoration(Object object) {
            this();
        }
    }
}

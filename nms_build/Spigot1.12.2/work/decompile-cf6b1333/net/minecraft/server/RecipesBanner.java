package net.minecraft.server;

import javax.annotation.Nullable;

public class RecipesBanner {    public static class AddRecipe implements IRecipe {

        public AddRecipe() {}

        public boolean a(InventoryCrafting inventorycrafting, World world) {
            boolean flag = false;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (itemstack.getItem() == Items.BANNER) {
                    if (flag) {
                        return false;
                    }

                    if (TileEntityBanner.b(itemstack) >= 6) {
                        return false;
                    }

                    flag = true;
                }
            }

            if (!flag) {
                return false;
            } else {
                return this.c(inventorycrafting) != null;
            }
        }

        public ItemStack craftItem(InventoryCrafting inventorycrafting) {
            ItemStack itemstack = ItemStack.a;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack1 = inventorycrafting.getItem(i);

                if (!itemstack1.isEmpty() && itemstack1.getItem() == Items.BANNER) {
                    itemstack = itemstack1.cloneItemStack();
                    itemstack.setCount(1);
                    break;
                }
            }

            EnumBannerPatternType enumbannerpatterntype = this.c(inventorycrafting);

            if (enumbannerpatterntype != null) {
                int j = 0;

                for (int k = 0; k < inventorycrafting.getSize(); ++k) {
                    ItemStack itemstack2 = inventorycrafting.getItem(k);

                    if (itemstack2.getItem() == Items.DYE) {
                        j = itemstack2.getData();
                        break;
                    }
                }

                NBTTagCompound nbttagcompound = itemstack.c("BlockEntityTag");
                NBTTagList nbttaglist;

                if (nbttagcompound.hasKeyOfType("Patterns", 9)) {
                    nbttaglist = nbttagcompound.getList("Patterns", 10);
                } else {
                    nbttaglist = new NBTTagList();
                    nbttagcompound.set("Patterns", nbttaglist);
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setString("Pattern", enumbannerpatterntype.b());
                nbttagcompound1.setInt("Color", j);
                nbttaglist.add(nbttagcompound1);
            }

            return itemstack;
        }

        public ItemStack b() {
            return ItemStack.a;
        }

        public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
            NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

            for (int i = 0; i < nonnulllist.size(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (itemstack.getItem().r()) {
                    nonnulllist.set(i, new ItemStack(itemstack.getItem().q()));
                }
            }

            return nonnulllist;
        }

        @Nullable
        private EnumBannerPatternType c(InventoryCrafting inventorycrafting) {
            EnumBannerPatternType[] aenumbannerpatterntype = EnumBannerPatternType.values();
            int i = aenumbannerpatterntype.length;

            for (int j = 0; j < i; ++j) {
                EnumBannerPatternType enumbannerpatterntype = aenumbannerpatterntype[j];

                if (enumbannerpatterntype.d()) {
                    boolean flag = true;
                    int k;

                    if (enumbannerpatterntype.e()) {
                        boolean flag1 = false;
                        boolean flag2 = false;

                        for (k = 0; k < inventorycrafting.getSize() && flag; ++k) {
                            ItemStack itemstack = inventorycrafting.getItem(k);

                            if (!itemstack.isEmpty() && itemstack.getItem() != Items.BANNER) {
                                if (itemstack.getItem() == Items.DYE) {
                                    if (flag2) {
                                        flag = false;
                                        break;
                                    }

                                    flag2 = true;
                                } else {
                                    if (flag1 || !itemstack.doMaterialsMatch(enumbannerpatterntype.f())) {
                                        flag = false;
                                        break;
                                    }

                                    flag1 = true;
                                }
                            }
                        }

                        if (!flag1 || !flag2) {
                            flag = false;
                        }
                    } else if (inventorycrafting.getSize() != enumbannerpatterntype.c().length * enumbannerpatterntype.c()[0].length()) {
                        flag = false;
                    } else {
                        int l = -1;

                        for (int i1 = 0; i1 < inventorycrafting.getSize() && flag; ++i1) {
                            k = i1 / 3;
                            int j1 = i1 % 3;
                            ItemStack itemstack1 = inventorycrafting.getItem(i1);

                            if (!itemstack1.isEmpty() && itemstack1.getItem() != Items.BANNER) {
                                if (itemstack1.getItem() != Items.DYE) {
                                    flag = false;
                                    break;
                                }

                                if (l != -1 && l != itemstack1.getData()) {
                                    flag = false;
                                    break;
                                }

                                if (enumbannerpatterntype.c()[k].charAt(j1) == 32) {
                                    flag = false;
                                    break;
                                }

                                l = itemstack1.getData();
                            } else if (enumbannerpatterntype.c()[k].charAt(j1) != 32) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    if (flag) {
                        return enumbannerpatterntype;
                    }
                }
            }

            return null;
        }

        public boolean c() {
            return true;
        }
    }

    public static class DuplicateRecipe implements IRecipe {

        public DuplicateRecipe() {}

        public boolean a(InventoryCrafting inventorycrafting, World world) {
            ItemStack itemstack = ItemStack.a;
            ItemStack itemstack1 = ItemStack.a;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack2 = inventorycrafting.getItem(i);

                if (!itemstack2.isEmpty()) {
                    if (itemstack2.getItem() != Items.BANNER) {
                        return false;
                    }

                    if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                        return false;
                    }

                    EnumColor enumcolor = ItemBanner.c(itemstack2);
                    boolean flag = TileEntityBanner.b(itemstack2) > 0;

                    if (!itemstack.isEmpty()) {
                        if (flag) {
                            return false;
                        }

                        if (enumcolor != ItemBanner.c(itemstack)) {
                            return false;
                        }

                        itemstack1 = itemstack2;
                    } else if (!itemstack1.isEmpty()) {
                        if (!flag) {
                            return false;
                        }

                        if (enumcolor != ItemBanner.c(itemstack1)) {
                            return false;
                        }

                        itemstack = itemstack2;
                    } else if (flag) {
                        itemstack = itemstack2;
                    } else {
                        itemstack1 = itemstack2;
                    }
                }
            }

            return !itemstack.isEmpty() && !itemstack1.isEmpty();
        }

        public ItemStack craftItem(InventoryCrafting inventorycrafting) {
            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (!itemstack.isEmpty() && TileEntityBanner.b(itemstack) > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    return itemstack1;
                }
            }

            return ItemStack.a;
        }

        public ItemStack b() {
            return ItemStack.a;
        }

        public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
            NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

            for (int i = 0; i < nonnulllist.size(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem().r()) {
                        nonnulllist.set(i, new ItemStack(itemstack.getItem().q()));
                    } else if (itemstack.hasTag() && TileEntityBanner.b(itemstack) > 0) {
                        ItemStack itemstack1 = itemstack.cloneItemStack();

                        itemstack1.setCount(1);
                        nonnulllist.set(i, itemstack1);
                    }
                }
            }

            return nonnulllist;
        }

        public boolean c() {
            return true;
        }
    }
}

package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBanner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityBanner;

public class RecipeBannerDuplicate extends IRecipeComplex {

    public RecipeBannerDuplicate(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        EnumColor enumcolor = null;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack2 = inventorycrafting.getItem(i);
            Item item = itemstack2.getItem();

            if (item instanceof ItemBanner) {
                ItemBanner itembanner = (ItemBanner) item;

                if (enumcolor == null) {
                    enumcolor = itembanner.b();
                } else if (enumcolor != itembanner.b()) {
                    return false;
                }

                int j = TileEntityBanner.b(itemstack2);

                if (j > 6) {
                    return false;
                }

                if (j > 0) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack2;
                } else {
                    if (itemstack1 != null) {
                        return false;
                    }

                    itemstack1 = itemstack2;
                }
            }
        }

        return itemstack != null && itemstack1 != null;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                int j = TileEntityBanner.b(itemstack);

                if (j > 0 && j <= 6) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    return itemstack1;
                }
            }
        }

        return ItemStack.b;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.b);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().p()) {
                    nonnulllist.set(i, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
                } else if (itemstack.hasTag() && TileEntityBanner.b(itemstack) > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    nonnulllist.set(i, itemstack1);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.k;
    }
}

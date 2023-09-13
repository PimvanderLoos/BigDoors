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

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        EnumColor enumcolor = null;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack2 = inventorycrafting.getItem(i);

            if (!itemstack2.isEmpty()) {
                Item item = itemstack2.getItem();

                if (!(item instanceof ItemBanner)) {
                    return false;
                }

                ItemBanner itembanner = (ItemBanner) item;

                if (enumcolor == null) {
                    enumcolor = itembanner.getColor();
                } else if (enumcolor != itembanner.getColor()) {
                    return false;
                }

                int j = TileEntityBanner.getPatternCount(itemstack2);

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

    public ItemStack assemble(InventoryCrafting inventorycrafting) {
        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                int j = TileEntityBanner.getPatternCount(itemstack);

                if (j > 0 && j <= 6) {
                    ItemStack itemstack1 = itemstack.copy();

                    itemstack1.setCount(1);
                    return itemstack1;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventorycrafting) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventorycrafting.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().hasCraftingRemainingItem()) {
                    nonnulllist.set(i, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
                } else if (itemstack.hasTag() && TileEntityBanner.getPatternCount(itemstack) > 0) {
                    ItemStack itemstack1 = itemstack.copy();

                    itemstack1.setCount(1);
                    nonnulllist.set(i, itemstack1);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }
}

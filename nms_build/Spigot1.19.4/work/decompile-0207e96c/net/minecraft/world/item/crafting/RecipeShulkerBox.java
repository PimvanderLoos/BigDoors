package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockShulkerBox;

public class RecipeShulkerBox extends IRecipeComplex {

    public RecipeShulkerBox(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < inventorycrafting.getContainerSize(); ++k) {
            ItemStack itemstack = inventorycrafting.getItem(k);

            if (!itemstack.isEmpty()) {
                if (Block.byItem(itemstack.getItem()) instanceof BlockShulkerBox) {
                    ++i;
                } else {
                    if (!(itemstack.getItem() instanceof ItemDye)) {
                        return false;
                    }

                    ++j;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }

        return i == 1 && j == 1;
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemDye itemdye = (ItemDye) Items.WHITE_DYE;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (Block.byItem(item) instanceof BlockShulkerBox) {
                    itemstack = itemstack1;
                } else if (item instanceof ItemDye) {
                    itemdye = (ItemDye) item;
                }
            }
        }

        ItemStack itemstack2 = BlockShulkerBox.getColoredItemStack(itemdye.getDyeColor());

        if (itemstack.hasTag()) {
            itemstack2.setTag(itemstack.getTag().copy());
        }

        return itemstack2;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHULKER_BOX_COLORING;
    }
}

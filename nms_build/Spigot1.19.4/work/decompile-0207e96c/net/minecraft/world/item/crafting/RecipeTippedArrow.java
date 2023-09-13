package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.level.World;

public class RecipeTippedArrow extends IRecipeComplex {

    public RecipeTippedArrow(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        if (inventorycrafting.getWidth() == 3 && inventorycrafting.getHeight() == 3) {
            for (int i = 0; i < inventorycrafting.getWidth(); ++i) {
                for (int j = 0; j < inventorycrafting.getHeight(); ++j) {
                    ItemStack itemstack = inventorycrafting.getItem(i + j * inventorycrafting.getWidth());

                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    if (i == 1 && j == 1) {
                        if (!itemstack.is(Items.LINGERING_POTION)) {
                            return false;
                        }
                    } else if (!itemstack.is(Items.ARROW)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = inventorycrafting.getItem(1 + inventorycrafting.getWidth());

        if (!itemstack.is(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);

            PotionUtil.setPotion(itemstack1, PotionUtil.getPotion(itemstack));
            PotionUtil.setCustomEffects(itemstack1, PotionUtil.getCustomEffects(itemstack));
            return itemstack1;
        }
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i >= 2 && j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}

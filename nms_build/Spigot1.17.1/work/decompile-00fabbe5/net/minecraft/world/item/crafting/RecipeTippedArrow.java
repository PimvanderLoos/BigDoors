package net.minecraft.world.item.crafting;

import java.util.Collection;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.level.World;

public class RecipeTippedArrow extends IRecipeComplex {

    public RecipeTippedArrow(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        if (inventorycrafting.g() == 3 && inventorycrafting.f() == 3) {
            for (int i = 0; i < inventorycrafting.g(); ++i) {
                for (int j = 0; j < inventorycrafting.f(); ++j) {
                    ItemStack itemstack = inventorycrafting.getItem(i + j * inventorycrafting.g());

                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    if (i == 1 && j == 1) {
                        if (!itemstack.a(Items.LINGERING_POTION)) {
                            return false;
                        }
                    } else if (!itemstack.a(Items.ARROW)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = inventorycrafting.getItem(1 + inventorycrafting.g());

        if (!itemstack.a(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);

            PotionUtil.a(itemstack1, PotionUtil.d(itemstack));
            PotionUtil.a(itemstack1, (Collection) PotionUtil.b(itemstack));
            return itemstack1;
        }
    }

    @Override
    public boolean a(int i, int j) {
        return i >= 2 && j >= 2;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}

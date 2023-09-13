package net.minecraft.world.item.crafting;

import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemSuspiciousStew;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class RecipeSuspiciousStew extends IRecipeComplex {

    public RecipeSuspiciousStew(MinecraftKey minecraftkey, CraftingBookCategory craftingbookcategory) {
        super(minecraftkey, craftingbookcategory);
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.is(Blocks.BROWN_MUSHROOM.asItem()) && !flag2) {
                    flag2 = true;
                } else if (itemstack.is(Blocks.RED_MUSHROOM.asItem()) && !flag1) {
                    flag1 = true;
                } else if (itemstack.is(TagsItem.SMALL_FLOWERS) && !flag) {
                    flag = true;
                } else {
                    if (!itemstack.is(Items.BOWL) || flag3) {
                        return false;
                    }

                    flag3 = true;
                }
            }
        }

        return flag && flag2 && flag1 && flag3;
    }

    public ItemStack assemble(InventoryCrafting inventorycrafting, IRegistryCustom iregistrycustom) {
        ItemStack itemstack = new ItemStack(Items.SUSPICIOUS_STEW, 1);

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                SuspiciousEffectHolder suspiciouseffectholder = SuspiciousEffectHolder.tryGet(itemstack1.getItem());

                if (suspiciouseffectholder != null) {
                    ItemSuspiciousStew.saveMobEffect(itemstack, suspiciouseffectholder.getSuspiciousEffect(), suspiciouseffectholder.getEffectDuration());
                    break;
                }
            }
        }

        return itemstack;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i >= 2 && j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}

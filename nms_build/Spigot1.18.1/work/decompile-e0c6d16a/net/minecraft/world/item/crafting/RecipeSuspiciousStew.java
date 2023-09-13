package net.minecraft.world.item.crafting;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemSuspiciousStew;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFlowers;
import net.minecraft.world.level.block.Blocks;

public class RecipeSuspiciousStew extends IRecipeComplex {

    public RecipeSuspiciousStew(MinecraftKey minecraftkey) {
        super(minecraftkey);
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
                } else if (itemstack.is((Tag) TagsItem.SMALL_FLOWERS) && !flag) {
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

    public ItemStack assemble(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < inventorycrafting.getContainerSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty() && itemstack1.is((Tag) TagsItem.SMALL_FLOWERS)) {
                itemstack = itemstack1;
                break;
            }
        }

        ItemStack itemstack2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);

        if (itemstack.getItem() instanceof ItemBlock && ((ItemBlock) itemstack.getItem()).getBlock() instanceof BlockFlowers) {
            BlockFlowers blockflowers = (BlockFlowers) ((ItemBlock) itemstack.getItem()).getBlock();
            MobEffectList mobeffectlist = blockflowers.getSuspiciousStewEffect();

            ItemSuspiciousStew.saveMobEffect(itemstack2, mobeffectlist, blockflowers.getEffectDuration());
        }

        return itemstack2;
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

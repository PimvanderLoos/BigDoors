package net.minecraft.world.item;

import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemLiquidUtil {

    public static InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.c(enumhand);
        return InteractionResultWrapper.consume(entityhuman.b(enumhand));
    }

    public static ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1, boolean flag) {
        boolean flag1 = entityhuman.abilities.canInstantlyBuild;

        if (flag && flag1) {
            if (!entityhuman.inventory.h(itemstack1)) {
                entityhuman.inventory.pickup(itemstack1);
            }

            return itemstack;
        } else {
            if (!flag1) {
                itemstack.subtract(1);
            }

            if (itemstack.isEmpty()) {
                return itemstack1;
            } else {
                if (!entityhuman.inventory.pickup(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                return itemstack;
            }
        }
    }

    public static ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        return a(itemstack, entityhuman, itemstack1, true);
    }
}

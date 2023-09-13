package net.minecraft.world.item;

import java.util.stream.Stream;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemLiquidUtil {

    public ItemLiquidUtil() {}

    public static InteractionResultWrapper<ItemStack> startUsingInstantly(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.startUsingItem(enumhand);
        return InteractionResultWrapper.consume(entityhuman.getItemInHand(enumhand));
    }

    public static ItemStack createFilledResult(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1, boolean flag) {
        boolean flag1 = entityhuman.getAbilities().instabuild;

        if (flag && flag1) {
            if (!entityhuman.getInventory().contains(itemstack1)) {
                entityhuman.getInventory().add(itemstack1);
            }

            return itemstack;
        } else {
            if (!flag1) {
                itemstack.shrink(1);
            }

            if (itemstack.isEmpty()) {
                return itemstack1;
            } else {
                if (!entityhuman.getInventory().add(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                return itemstack;
            }
        }
    }

    public static ItemStack createFilledResult(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        return createFilledResult(itemstack, entityhuman, itemstack1, true);
    }

    public static void onContainerDestroyed(EntityItem entityitem, Stream<ItemStack> stream) {
        World world = entityitem.level;

        if (!world.isClientSide) {
            stream.forEach((itemstack) -> {
                world.addFreshEntity(new EntityItem(world, entityitem.getX(), entityitem.getY(), entityitem.getZ(), itemstack));
            });
        }
    }
}

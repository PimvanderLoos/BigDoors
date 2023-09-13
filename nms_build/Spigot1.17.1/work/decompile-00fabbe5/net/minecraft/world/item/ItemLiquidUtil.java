package net.minecraft.world.item;

import java.util.stream.Stream;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemLiquidUtil {

    public ItemLiquidUtil() {}

    public static InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.c(enumhand);
        return InteractionResultWrapper.consume(entityhuman.b(enumhand));
    }

    public static ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1, boolean flag) {
        boolean flag1 = entityhuman.getAbilities().instabuild;

        if (flag && flag1) {
            if (!entityhuman.getInventory().h(itemstack1)) {
                entityhuman.getInventory().pickup(itemstack1);
            }

            return itemstack;
        } else {
            if (!flag1) {
                itemstack.subtract(1);
            }

            if (itemstack.isEmpty()) {
                return itemstack1;
            } else {
                if (!entityhuman.getInventory().pickup(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }

                return itemstack;
            }
        }
    }

    public static ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        return a(itemstack, entityhuman, itemstack1, true);
    }

    public static void a(EntityItem entityitem, Stream<ItemStack> stream) {
        World world = entityitem.level;

        if (!world.isClientSide) {
            stream.forEach((itemstack) -> {
                world.addEntity(new EntityItem(world, entityitem.locX(), entityitem.locY(), entityitem.locZ(), itemstack));
            });
        }
    }
}

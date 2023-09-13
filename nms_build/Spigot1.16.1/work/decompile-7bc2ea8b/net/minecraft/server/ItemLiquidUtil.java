package net.minecraft.server;

public class ItemLiquidUtil {

    public static InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.c(enumhand);
        return InteractionResultWrapper.consume(entityhuman.b(enumhand));
    }

    public static ItemStack a(ItemStack itemstack, EntityHuman entityhuman, ItemStack itemstack1) {
        if (entityhuman.abilities.canInstantlyBuild) {
            if (!entityhuman.inventory.h(itemstack1)) {
                entityhuman.inventory.pickup(itemstack1);
            }

            return itemstack;
        } else {
            itemstack.subtract(1);
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
}

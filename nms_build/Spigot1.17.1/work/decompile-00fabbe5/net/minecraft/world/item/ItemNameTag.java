package net.minecraft.world.item;

import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;

public class ItemNameTag extends Item {

    public ItemNameTag(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (itemstack.hasName() && !(entityliving instanceof EntityHuman)) {
            if (!entityhuman.level.isClientSide && entityliving.isAlive()) {
                entityliving.setCustomName(itemstack.getName());
                if (entityliving instanceof EntityInsentient) {
                    ((EntityInsentient) entityliving).setPersistent();
                }

                itemstack.subtract(1);
            }

            return EnumInteractionResult.a(entityhuman.level.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }
}

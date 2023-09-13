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
    public EnumInteractionResult interactLivingEntity(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (itemstack.hasCustomHoverName() && !(entityliving instanceof EntityHuman)) {
            if (!entityhuman.level.isClientSide && entityliving.isAlive()) {
                entityliving.setCustomName(itemstack.getHoverName());
                if (entityliving instanceof EntityInsentient) {
                    ((EntityInsentient) entityliving).setPersistenceRequired();
                }

                itemstack.shrink(1);
            }

            return EnumInteractionResult.sidedSuccess(entityhuman.level.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }
}

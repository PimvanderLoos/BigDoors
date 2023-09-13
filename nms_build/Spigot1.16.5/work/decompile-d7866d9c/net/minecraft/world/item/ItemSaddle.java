package net.minecraft.world.item;

import net.minecraft.sounds.SoundCategory;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.player.EntityHuman;

public class ItemSaddle extends Item {

    public ItemSaddle(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (entityliving instanceof ISaddleable && entityliving.isAlive()) {
            ISaddleable isaddleable = (ISaddleable) entityliving;

            if (!isaddleable.hasSaddle() && isaddleable.canSaddle()) {
                if (!entityhuman.world.isClientSide) {
                    isaddleable.saddle(SoundCategory.NEUTRAL);
                    itemstack.subtract(1);
                }

                return EnumInteractionResult.a(entityhuman.world.isClientSide);
            }
        }

        return EnumInteractionResult.PASS;
    }
}

package net.minecraft.world.item;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemMilkBucket extends Item {

    private static final int DRINK_DURATION = 32;

    public ItemMilkBucket(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityliving;

            CriterionTriggers.CONSUME_ITEM.trigger(entityplayer, itemstack);
            entityplayer.awardStat(StatisticList.ITEM_USED.get(this));
        }

        if (entityliving instanceof EntityHuman && !((EntityHuman) entityliving).getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        if (!world.isClientSide) {
            entityliving.removeAllEffects();
        }

        return itemstack.isEmpty() ? new ItemStack(Items.BUCKET) : itemstack;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 32;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return ItemLiquidUtil.startUsingInstantly(world, entityhuman, enumhand);
    }
}

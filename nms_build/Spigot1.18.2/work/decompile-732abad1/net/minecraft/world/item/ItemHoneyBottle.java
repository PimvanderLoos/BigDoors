package net.minecraft.world.item;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemHoneyBottle extends Item {

    private static final int DRINK_DURATION = 40;

    public ItemHoneyBottle(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        super.finishUsingItem(itemstack, world, entityliving);
        if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityliving;

            CriterionTriggers.CONSUME_ITEM.trigger(entityplayer, itemstack);
            entityplayer.awardStat(StatisticList.ITEM_USED.get(this));
        }

        if (!world.isClientSide) {
            entityliving.removeEffect(MobEffects.POISON);
        }

        if (itemstack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (entityliving instanceof EntityHuman && !((EntityHuman) entityliving).getAbilities().instabuild) {
                ItemStack itemstack1 = new ItemStack(Items.GLASS_BOTTLE);
                EntityHuman entityhuman = (EntityHuman) entityliving;

                if (!entityhuman.getInventory().add(itemstack1)) {
                    entityhuman.drop(itemstack1, false);
                }
            }

            return itemstack;
        }
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 40;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    @Override
    public SoundEffect getDrinkingSound() {
        return SoundEffects.HONEY_DRINK;
    }

    @Override
    public SoundEffect getEatingSound() {
        return SoundEffects.HONEY_DRINK;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return ItemLiquidUtil.startUsingInstantly(world, entityhuman, enumhand);
    }
}

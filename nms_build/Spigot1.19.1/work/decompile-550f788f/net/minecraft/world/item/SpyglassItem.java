package net.minecraft.world.item;

import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class SpyglassItem extends Item {

    public static final int USE_DURATION = 1200;
    public static final float ZOOM_FOV_MODIFIER = 0.1F;

    public SpyglassItem(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 1200;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.SPYGLASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        entityhuman.playSound(SoundEffects.SPYGLASS_USE, 1.0F, 1.0F);
        entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
        return ItemLiquidUtil.startUsingInstantly(world, entityhuman, enumhand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        this.stopUsing(entityliving);
        return itemstack;
    }

    @Override
    public void releaseUsing(ItemStack itemstack, World world, EntityLiving entityliving, int i) {
        this.stopUsing(entityliving);
    }

    private void stopUsing(EntityLiving entityliving) {
        entityliving.playSound(SoundEffects.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    }
}

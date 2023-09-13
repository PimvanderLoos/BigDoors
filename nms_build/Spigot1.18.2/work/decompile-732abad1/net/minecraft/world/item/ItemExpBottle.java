package net.minecraft.world.item;

import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityThrownExpBottle;
import net.minecraft.world.level.World;

public class ItemExpBottle extends Item {

    public ItemExpBottle(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        world.playSound((EntityHuman) null, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide) {
            EntityThrownExpBottle entitythrownexpbottle = new EntityThrownExpBottle(world, entityhuman);

            entitythrownexpbottle.setItem(itemstack);
            entitythrownexpbottle.shootFromRotation(entityhuman, entityhuman.getXRot(), entityhuman.getYRot(), -20.0F, 0.7F, 1.0F);
            world.addFreshEntity(entitythrownexpbottle);
        }

        entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
        if (!entityhuman.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
    }
}

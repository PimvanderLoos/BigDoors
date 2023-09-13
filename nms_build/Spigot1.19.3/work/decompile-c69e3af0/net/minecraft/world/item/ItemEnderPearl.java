package net.minecraft.world.item;

import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityEnderPearl;
import net.minecraft.world.level.World;

public class ItemEnderPearl extends Item {

    public ItemEnderPearl(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        world.playSound((EntityHuman) null, entityhuman.getX(), entityhuman.getY(), entityhuman.getZ(), SoundEffects.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        entityhuman.getCooldowns().addCooldown(this, 20);
        if (!world.isClientSide) {
            EntityEnderPearl entityenderpearl = new EntityEnderPearl(world, entityhuman);

            entityenderpearl.setItem(itemstack);
            entityenderpearl.shootFromRotation(entityhuman, entityhuman.getXRot(), entityhuman.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(entityenderpearl);
        }

        entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
        if (!entityhuman.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
    }
}

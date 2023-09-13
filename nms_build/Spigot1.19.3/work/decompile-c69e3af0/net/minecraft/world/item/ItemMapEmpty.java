package net.minecraft.world.item;

import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemMapEmpty extends ItemWorldMapBase {

    public ItemMapEmpty(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (world.isClientSide) {
            return InteractionResultWrapper.success(itemstack);
        } else {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            entityhuman.level.playSound((EntityHuman) null, (Entity) entityhuman, SoundEffects.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, entityhuman.getSoundSource(), 1.0F, 1.0F);
            ItemStack itemstack1 = ItemWorldMap.create(world, entityhuman.getBlockX(), entityhuman.getBlockZ(), (byte) 0, true, false);

            if (itemstack.isEmpty()) {
                return InteractionResultWrapper.consume(itemstack1);
            } else {
                if (!entityhuman.getInventory().add(itemstack1.copy())) {
                    entityhuman.drop(itemstack1, false);
                }

                return InteractionResultWrapper.consume(itemstack);
            }
        }
    }
}

package net.minecraft.world.item;

import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ISteerable;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemCarrotStick<T extends Entity & ISteerable> extends Item {

    private final EntityTypes<T> canInteractWith;
    private final int consumeItemDamage;

    public ItemCarrotStick(Item.Info item_info, EntityTypes<T> entitytypes, int i) {
        super(item_info);
        this.canInteractWith = entitytypes;
        this.consumeItemDamage = i;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (world.isClientSide) {
            return InteractionResultWrapper.pass(itemstack);
        } else {
            Entity entity = entityhuman.getControlledVehicle();

            if (entityhuman.isPassenger() && entity instanceof ISteerable) {
                ISteerable isteerable = (ISteerable) entity;

                if (entity.getType() == this.canInteractWith && isteerable.boost()) {
                    itemstack.hurtAndBreak(this.consumeItemDamage, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastBreakEvent(enumhand);
                    });
                    if (itemstack.isEmpty()) {
                        ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);

                        itemstack1.setTag(itemstack.getTag());
                        return InteractionResultWrapper.success(itemstack1);
                    }

                    return InteractionResultWrapper.success(itemstack);
                }
            }

            entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            return InteractionResultWrapper.pass(itemstack);
        }
    }
}

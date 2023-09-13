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

    private final EntityTypes<T> a;
    private final int b;

    public ItemCarrotStick(Item.Info item_info, EntityTypes<T> entitytypes, int i) {
        super(item_info);
        this.a = entitytypes;
        this.b = i;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (world.isClientSide) {
            return InteractionResultWrapper.pass(itemstack);
        } else {
            Entity entity = entityhuman.getVehicle();

            if (entityhuman.isPassenger() && entity instanceof ISteerable && entity.getEntityType() == this.a) {
                ISteerable isteerable = (ISteerable) entity;

                if (isteerable.O_()) {
                    itemstack.damage(this.b, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastItemBreak(enumhand);
                    });
                    if (itemstack.isEmpty()) {
                        ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);

                        itemstack1.setTag(itemstack.getTag());
                        return InteractionResultWrapper.success(itemstack1);
                    }

                    return InteractionResultWrapper.success(itemstack);
                }
            }

            entityhuman.b(StatisticList.ITEM_USED.b(this));
            return InteractionResultWrapper.pass(itemstack);
        }
    }
}

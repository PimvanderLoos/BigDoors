package net.minecraft.world.item;

import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemMapEmpty extends ItemWorldMapBase {

    public ItemMapEmpty(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = ItemWorldMap.createFilledMapView(world, MathHelper.floor(entityhuman.locX()), MathHelper.floor(entityhuman.locZ()), (byte) 0, true, false);
        ItemStack itemstack1 = entityhuman.b(enumhand);

        if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack1.subtract(1);
        }

        entityhuman.b(StatisticList.ITEM_USED.b(this));
        entityhuman.playSound(SoundEffects.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 1.0F);
        if (itemstack1.isEmpty()) {
            return InteractionResultWrapper.a(itemstack, world.s_());
        } else {
            if (!entityhuman.inventory.pickup(itemstack.cloneItemStack())) {
                entityhuman.drop(itemstack, false);
            }

            return InteractionResultWrapper.a(itemstack1, world.s_());
        }
    }
}

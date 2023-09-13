package net.minecraft.world.item;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemSoup extends Item {

    public ItemSoup(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        ItemStack itemstack1 = super.a(itemstack, world, entityliving);

        return entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.canInstantlyBuild ? itemstack1 : new ItemStack(Items.BOWL);
    }
}

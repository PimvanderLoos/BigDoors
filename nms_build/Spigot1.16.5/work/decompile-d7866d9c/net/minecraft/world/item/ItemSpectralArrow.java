package net.minecraft.world.item;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntitySpectralArrow;
import net.minecraft.world.level.World;

public class ItemSpectralArrow extends ItemArrow {

    public ItemSpectralArrow(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EntityArrow a(World world, ItemStack itemstack, EntityLiving entityliving) {
        return new EntitySpectralArrow(world, entityliving);
    }
}

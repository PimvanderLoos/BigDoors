package net.minecraft.core.dispenser;

import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.core.ISourceBlock;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;

public abstract class DispenseBehaviorProjectile extends DispenseBehaviorItem {

    public DispenseBehaviorProjectile() {}

    @Override
    public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        WorldServer worldserver = isourceblock.getWorld();
        IPosition iposition = BlockDispenser.a(isourceblock);
        EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
        IProjectile iprojectile = this.a((World) worldserver, iposition, itemstack);

        iprojectile.shoot((double) enumdirection.getAdjacentX(), (double) ((float) enumdirection.getAdjacentY() + 0.1F), (double) enumdirection.getAdjacentZ(), this.getPower(), this.a());
        worldserver.addEntity(iprojectile);
        itemstack.subtract(1);
        return itemstack;
    }

    @Override
    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(1002, isourceblock.getBlockPosition(), 0);
    }

    protected abstract IProjectile a(World world, IPosition iposition, ItemStack itemstack);

    protected float a() {
        return 6.0F;
    }

    protected float getPower() {
        return 1.1F;
    }
}

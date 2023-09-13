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
    public ItemStack execute(ISourceBlock isourceblock, ItemStack itemstack) {
        WorldServer worldserver = isourceblock.getLevel();
        IPosition iposition = BlockDispenser.getDispensePosition(isourceblock);
        EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockState().getValue(BlockDispenser.FACING);
        IProjectile iprojectile = this.getProjectile(worldserver, iposition, itemstack);

        iprojectile.shoot((double) enumdirection.getStepX(), (double) ((float) enumdirection.getStepY() + 0.1F), (double) enumdirection.getStepZ(), this.getPower(), this.getUncertainty());
        worldserver.addFreshEntity(iprojectile);
        itemstack.shrink(1);
        return itemstack;
    }

    @Override
    protected void playSound(ISourceBlock isourceblock) {
        isourceblock.getLevel().levelEvent(1002, isourceblock.getPos(), 0);
    }

    protected abstract IProjectile getProjectile(World world, IPosition iposition, ItemStack itemstack);

    protected float getUncertainty() {
        return 6.0F;
    }

    protected float getPower() {
        return 1.1F;
    }
}

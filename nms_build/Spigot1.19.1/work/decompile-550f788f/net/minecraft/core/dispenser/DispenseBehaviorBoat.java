package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BlockDispenser;

public class DispenseBehaviorBoat extends DispenseBehaviorItem {

    private final DispenseBehaviorItem defaultDispenseItemBehavior;
    private final EntityBoat.EnumBoatType type;
    private final boolean isChestBoat;

    public DispenseBehaviorBoat(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this(entityboat_enumboattype, false);
    }

    public DispenseBehaviorBoat(EntityBoat.EnumBoatType entityboat_enumboattype, boolean flag) {
        this.defaultDispenseItemBehavior = new DispenseBehaviorItem();
        this.type = entityboat_enumboattype;
        this.isChestBoat = flag;
    }

    @Override
    public ItemStack execute(ISourceBlock isourceblock, ItemStack itemstack) {
        EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockState().getValue(BlockDispenser.FACING);
        WorldServer worldserver = isourceblock.getLevel();
        double d0 = isourceblock.x() + (double) ((float) enumdirection.getStepX() * 1.125F);
        double d1 = isourceblock.y() + (double) ((float) enumdirection.getStepY() * 1.125F);
        double d2 = isourceblock.z() + (double) ((float) enumdirection.getStepZ() * 1.125F);
        BlockPosition blockposition = isourceblock.getPos().relative(enumdirection);
        double d3;

        if (worldserver.getFluidState(blockposition).is(TagsFluid.WATER)) {
            d3 = 1.0D;
        } else {
            if (!worldserver.getBlockState(blockposition).isAir() || !worldserver.getFluidState(blockposition.below()).is(TagsFluid.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(isourceblock, itemstack);
            }

            d3 = 0.0D;
        }

        Object object = this.isChestBoat ? new ChestBoat(worldserver, d0, d1 + d3, d2) : new EntityBoat(worldserver, d0, d1 + d3, d2);

        ((EntityBoat) object).setType(this.type);
        ((EntityBoat) object).setYRot(enumdirection.toYRot());
        worldserver.addFreshEntity((Entity) object);
        itemstack.shrink(1);
        return itemstack;
    }

    @Override
    protected void playSound(ISourceBlock isourceblock) {
        isourceblock.getLevel().levelEvent(1000, isourceblock.getPos(), 0);
    }
}

package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BlockDispenser;

public class DispenseBehaviorBoat extends DispenseBehaviorItem {

    private final DispenseBehaviorItem defaultDispenseItemBehavior = new DispenseBehaviorItem();
    private final EntityBoat.EnumBoatType type;

    public DispenseBehaviorBoat(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.type = entityboat_enumboattype;
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

        if (worldserver.getFluidState(blockposition).is((Tag) TagsFluid.WATER)) {
            d3 = 1.0D;
        } else {
            if (!worldserver.getBlockState(blockposition).isAir() || !worldserver.getFluidState(blockposition.below()).is((Tag) TagsFluid.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(isourceblock, itemstack);
            }

            d3 = 0.0D;
        }

        EntityBoat entityboat = new EntityBoat(worldserver, d0, d1 + d3, d2);

        entityboat.setType(this.type);
        entityboat.setYRot(enumdirection.toYRot());
        worldserver.addFreshEntity(entityboat);
        itemstack.shrink(1);
        return itemstack;
    }

    @Override
    protected void playSound(ISourceBlock isourceblock) {
        isourceblock.getLevel().levelEvent(1000, isourceblock.getPos(), 0);
    }
}

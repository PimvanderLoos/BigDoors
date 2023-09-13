package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SourceBlock;
import net.minecraft.core.dispenser.DispenseBehaviorItem;
import net.minecraft.core.dispenser.IDispenseBehavior;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityDispenser;
import net.minecraft.world.level.block.entity.TileEntityDropper;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockDropper extends BlockDispenser {

    private static final IDispenseBehavior DISPENSE_BEHAVIOUR = new DispenseBehaviorItem();

    public BlockDropper(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    protected IDispenseBehavior getDispenseMethod(ItemStack itemstack) {
        return BlockDropper.DISPENSE_BEHAVIOUR;
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityDropper(blockposition, iblockdata);
    }

    @Override
    public void dispenseFrom(WorldServer worldserver, BlockPosition blockposition) {
        SourceBlock sourceblock = new SourceBlock(worldserver, blockposition);
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser) sourceblock.getEntity();
        int i = tileentitydispenser.getRandomSlot(worldserver.random);

        if (i < 0) {
            worldserver.levelEvent(1001, blockposition, 0);
        } else {
            ItemStack itemstack = tileentitydispenser.getItem(i);

            if (!itemstack.isEmpty()) {
                EnumDirection enumdirection = (EnumDirection) worldserver.getBlockState(blockposition).getValue(BlockDropper.FACING);
                IInventory iinventory = TileEntityHopper.getContainerAt(worldserver, blockposition.relative(enumdirection));
                ItemStack itemstack1;

                if (iinventory == null) {
                    itemstack1 = BlockDropper.DISPENSE_BEHAVIOUR.dispense(sourceblock, itemstack);
                } else {
                    itemstack1 = TileEntityHopper.addItem(tileentitydispenser, iinventory, itemstack.copy().split(1), enumdirection.getOpposite());
                    if (itemstack1.isEmpty()) {
                        itemstack1 = itemstack.copy();
                        itemstack1.shrink(1);
                    } else {
                        itemstack1 = itemstack.copy();
                    }
                }

                tileentitydispenser.setItem(i, itemstack1);
            }
        }
    }
}

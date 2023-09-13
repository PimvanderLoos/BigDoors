package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityChestTrapped extends TileEntityChest {

    public TileEntityChestTrapped(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.TRAPPED_CHEST, blockposition, iblockdata);
    }

    @Override
    protected void signalOpenCount(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        super.signalOpenCount(world, blockposition, iblockdata, i, j);
        if (i != j) {
            Block block = iblockdata.getBlock();

            world.updateNeighborsAt(blockposition, block);
            world.updateNeighborsAt(blockposition.below(), block);
        }

    }
}

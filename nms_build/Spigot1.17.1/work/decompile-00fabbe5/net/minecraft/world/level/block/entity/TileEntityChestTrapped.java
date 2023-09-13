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
    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        super.a(world, blockposition, iblockdata, i, j);
        if (i != j) {
            Block block = iblockdata.getBlock();

            world.applyPhysics(blockposition, block);
            world.applyPhysics(blockposition.down(), block);
        }

    }
}

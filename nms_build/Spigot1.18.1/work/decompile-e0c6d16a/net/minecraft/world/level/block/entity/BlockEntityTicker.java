package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

@FunctionalInterface
public interface BlockEntityTicker<T extends TileEntity> {

    void tick(World world, BlockPosition blockposition, IBlockData iblockdata, T t0);
}

package net.minecraft.world.level.chunk;

import net.minecraft.world.level.block.state.IBlockData;

public interface BlockColumn {

    IBlockData getBlock(int i);

    void setBlock(int i, IBlockData iblockdata);
}

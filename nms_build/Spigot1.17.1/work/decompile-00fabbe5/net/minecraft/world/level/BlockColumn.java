package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public final class BlockColumn {

    private final int minY;
    private final IBlockData[] column;

    public BlockColumn(int i, IBlockData[] aiblockdata) {
        this.minY = i;
        this.column = aiblockdata;
    }

    public IBlockData a(BlockPosition blockposition) {
        int i = blockposition.getY() - this.minY;

        return i >= 0 && i < this.column.length ? this.column[i] : Blocks.AIR.getBlockData();
    }
}

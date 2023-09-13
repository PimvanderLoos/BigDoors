package net.minecraft.world.level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public final class BlockColumn implements net.minecraft.world.level.chunk.BlockColumn {

    private final int minY;
    private final IBlockData[] column;

    public BlockColumn(int i, IBlockData[] aiblockdata) {
        this.minY = i;
        this.column = aiblockdata;
    }

    @Override
    public IBlockData getBlock(int i) {
        int j = i - this.minY;

        return j >= 0 && j < this.column.length ? this.column[j] : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void setBlock(int i, IBlockData iblockdata) {
        int j = i - this.minY;

        if (j >= 0 && j < this.column.length) {
            this.column[j] = iblockdata;
        } else {
            throw new IllegalArgumentException("Outside of column height: " + i);
        }
    }
}

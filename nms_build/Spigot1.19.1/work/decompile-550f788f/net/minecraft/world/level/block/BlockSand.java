package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockSand extends BlockFalling {

    private final int dustColor;

    public BlockSand(int i, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.dustColor = i;
    }

    @Override
    public int getDustColor(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.dustColor;
    }
}

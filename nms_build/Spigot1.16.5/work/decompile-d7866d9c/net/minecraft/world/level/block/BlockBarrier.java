package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockBarrier extends Block {

    protected BlockBarrier(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }
}

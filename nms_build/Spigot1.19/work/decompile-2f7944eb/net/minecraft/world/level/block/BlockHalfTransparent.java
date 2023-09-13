package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockHalfTransparent extends Block {

    protected BlockHalfTransparent(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean skipRendering(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return iblockdata1.is((Block) this) ? true : super.skipRendering(iblockdata, iblockdata1, enumdirection);
    }
}

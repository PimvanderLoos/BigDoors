package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockHalfTransparent extends Block {

    protected BlockHalfTransparent(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return iblockdata1.a((Block) this) ? true : super.a(iblockdata, iblockdata1, enumdirection);
    }
}

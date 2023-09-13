package net.minecraft.server;

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

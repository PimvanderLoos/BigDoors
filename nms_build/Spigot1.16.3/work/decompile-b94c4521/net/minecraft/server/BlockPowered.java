package net.minecraft.server;

public class BlockPowered extends Block {

    public BlockPowered(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 15;
    }
}

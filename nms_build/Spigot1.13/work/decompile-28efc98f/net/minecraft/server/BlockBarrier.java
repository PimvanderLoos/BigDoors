package net.minecraft.server;

public class BlockBarrier extends Block {

    protected BlockBarrier(Block.Info block_info) {
        super(block_info);
    }

    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public boolean f(IBlockData iblockdata) {
        return false;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {}
}

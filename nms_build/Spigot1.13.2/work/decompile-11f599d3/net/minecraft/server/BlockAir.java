package net.minecraft.server;

public class BlockAir extends Block {

    protected BlockAir(Block.Info block_info) {
        super(block_info);
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    public boolean isCollidable(IBlockData iblockdata) {
        return false;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {}

    public boolean e(IBlockData iblockdata) {
        return true;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}

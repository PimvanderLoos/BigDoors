package net.minecraft.server;

public class BlockAir extends Block {

    protected BlockAir(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }
}

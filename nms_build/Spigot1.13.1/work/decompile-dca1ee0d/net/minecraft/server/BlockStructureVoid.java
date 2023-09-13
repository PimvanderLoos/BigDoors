package net.minecraft.server;

public class BlockStructureVoid extends Block {

    private static final VoxelShape a = Block.a(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

    protected BlockStructureVoid(Block.Info block_info) {
        super(block_info);
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockStructureVoid.a;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {}

    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}

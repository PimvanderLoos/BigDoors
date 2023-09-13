package net.minecraft.server;

public class BlockCoralFanAbstract extends BlockCoralBase {

    private static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    protected BlockCoralFanAbstract(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCoralFanAbstract.a;
    }
}

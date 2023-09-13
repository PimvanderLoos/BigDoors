package net.minecraft.server;

public abstract class BlockSkullAbstract extends BlockTileEntity implements ItemWearable {

    private final BlockSkull.a a;

    public BlockSkullAbstract(BlockSkull.a blockskull_a, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.a = blockskull_a;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntitySkull();
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

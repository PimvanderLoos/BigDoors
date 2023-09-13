package net.minecraft.server;

public class BlockEndGateway extends BlockTileEntity {

    protected BlockEndGateway(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityEndGateway();
    }

    @Override
    public boolean a(IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }
}

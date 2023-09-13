package net.minecraft.server;

public class BlockFurnaceFurace extends BlockFurnace {

    protected BlockFurnaceFurace(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityFurnaceFurnace();
    }

    @Override
    protected void a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityFurnaceFurnace) {
            entityhuman.openContainer((ITileInventory) tileentity);
            entityhuman.a(StatisticList.INTERACT_WITH_FURNACE);
        }

    }
}

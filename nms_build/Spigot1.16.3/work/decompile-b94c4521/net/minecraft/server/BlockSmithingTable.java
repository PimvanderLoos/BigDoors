package net.minecraft.server;

public class BlockSmithingTable extends BlockWorkbench {

    private static final IChatBaseComponent a = new ChatMessage("container.upgrade");

    protected BlockSmithingTable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerSmithing(i, playerinventory, ContainerAccess.at(world, blockposition));
        }, BlockSmithingTable.a);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_SMITHING_TABLE);
            return EnumInteractionResult.CONSUME;
        }
    }
}

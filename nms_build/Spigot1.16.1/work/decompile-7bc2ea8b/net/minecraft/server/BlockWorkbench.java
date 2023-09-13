package net.minecraft.server;

public class BlockWorkbench extends Block {

    private static final IChatBaseComponent a = new ChatMessage("container.crafting");

    protected BlockWorkbench(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_CRAFTING_TABLE);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerWorkbench(i, playerinventory, ContainerAccess.at(world, blockposition));
        }, BlockWorkbench.a);
    }
}

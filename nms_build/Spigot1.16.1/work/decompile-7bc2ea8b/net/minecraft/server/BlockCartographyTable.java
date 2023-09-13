package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockCartographyTable extends Block {

    private static final ChatMessage a = new ChatMessage("container.cartography_table");

    protected BlockCartographyTable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_CARTOGRAPHY_TABLE);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerCartography(i, playerinventory, ContainerAccess.at(world, blockposition));
        }, BlockCartographyTable.a);
    }
}

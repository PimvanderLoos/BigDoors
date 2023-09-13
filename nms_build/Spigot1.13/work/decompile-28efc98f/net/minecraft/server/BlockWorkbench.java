package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockWorkbench extends Block {

    protected BlockWorkbench(Block.Info block_info) {
        super(block_info);
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            entityhuman.openTileEntity(new BlockWorkbench.TileEntityContainerWorkbench(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_CRAFTING_TABLE);
            return true;
        }
    }

    public static class TileEntityContainerWorkbench implements ITileEntityContainer {

        private final World a;
        private final BlockPosition b;

        public TileEntityContainerWorkbench(World world, BlockPosition blockposition) {
            this.a = world;
            this.b = blockposition;
        }

        public IChatBaseComponent getDisplayName() {
            return new ChatMessage(Blocks.CRAFTING_TABLE.m() + ".name", new Object[0]);
        }

        public boolean hasCustomName() {
            return false;
        }

        @Nullable
        public IChatBaseComponent getCustomName() {
            return null;
        }

        public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
            return new ContainerWorkbench(playerinventory, this.a, this.b);
        }

        public String getContainerName() {
            return "minecraft:crafting_table";
        }
    }
}

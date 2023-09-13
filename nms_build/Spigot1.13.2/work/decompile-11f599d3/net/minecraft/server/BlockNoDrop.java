package net.minecraft.server;

public class BlockNoDrop extends Block {

    public BlockNoDrop(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }
}

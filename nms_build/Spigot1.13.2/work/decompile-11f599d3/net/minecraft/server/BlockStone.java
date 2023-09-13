package net.minecraft.server;

public class BlockStone extends Block {

    public BlockStone(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Blocks.COBBLESTONE;
    }
}

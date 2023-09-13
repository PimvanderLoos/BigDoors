package net.minecraft.server;

public class BlockGlassPane extends BlockIronBars {

    protected BlockGlassPane(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }
}

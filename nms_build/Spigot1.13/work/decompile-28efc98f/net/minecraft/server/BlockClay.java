package net.minecraft.server;

import java.util.Random;

public class BlockClay extends Block {

    public BlockClay(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.CLAY_BALL;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 4;
    }
}

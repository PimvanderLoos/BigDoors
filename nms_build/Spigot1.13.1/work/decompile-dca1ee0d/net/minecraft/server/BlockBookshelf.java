package net.minecraft.server;

import java.util.Random;

public class BlockBookshelf extends Block {

    public BlockBookshelf(Block.Info block_info) {
        super(block_info);
    }

    public int a(IBlockData iblockdata, Random random) {
        return 3;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.BOOK;
    }
}

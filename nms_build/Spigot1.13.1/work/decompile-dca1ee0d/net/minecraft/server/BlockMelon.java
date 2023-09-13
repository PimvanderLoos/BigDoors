package net.minecraft.server;

import java.util.Random;

public class BlockMelon extends BlockStemmed {

    protected BlockMelon(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.MELON_SLICE;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 3 + random.nextInt(5);
    }

    public int getDropCount(IBlockData iblockdata, int i, World world, BlockPosition blockposition, Random random) {
        return Math.min(9, this.a(iblockdata, random) + random.nextInt(1 + i));
    }

    public BlockStem d() {
        return (BlockStem) Blocks.MELON_STEM;
    }

    public BlockStemAttached e() {
        return (BlockStemAttached) Blocks.ATTACHED_MELON_STEM;
    }
}

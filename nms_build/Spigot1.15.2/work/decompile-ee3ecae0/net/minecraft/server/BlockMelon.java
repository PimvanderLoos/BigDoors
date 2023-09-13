package net.minecraft.server;

public class BlockMelon extends BlockStemmed {

    protected BlockMelon(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public BlockStem c() {
        return (BlockStem) Blocks.MELON_STEM;
    }

    @Override
    public BlockStemAttached d() {
        return (BlockStemAttached) Blocks.ATTACHED_MELON_STEM;
    }
}

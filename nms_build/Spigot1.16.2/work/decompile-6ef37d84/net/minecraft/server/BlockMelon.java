package net.minecraft.server;

public class BlockMelon extends BlockStemmed {

    protected BlockMelon(BlockBase.Info blockbase_info) {
        super(blockbase_info);
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

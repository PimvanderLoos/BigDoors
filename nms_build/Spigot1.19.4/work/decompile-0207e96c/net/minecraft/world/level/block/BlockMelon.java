package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBase;

public class BlockMelon extends BlockStemmed {

    protected BlockMelon(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public BlockStem getStem() {
        return (BlockStem) Blocks.MELON_STEM;
    }

    @Override
    public BlockStemAttached getAttachedStem() {
        return (BlockStemAttached) Blocks.ATTACHED_MELON_STEM;
    }
}

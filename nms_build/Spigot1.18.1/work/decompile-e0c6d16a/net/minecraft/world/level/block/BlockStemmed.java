package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBase;

public abstract class BlockStemmed extends Block {

    public BlockStemmed(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    public abstract BlockStem getStem();

    public abstract BlockStemAttached getAttachedStem();
}

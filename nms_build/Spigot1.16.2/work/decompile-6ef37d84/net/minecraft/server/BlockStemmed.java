package net.minecraft.server;

public abstract class BlockStemmed extends Block {

    public BlockStemmed(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    public abstract BlockStem c();

    public abstract BlockStemAttached d();
}

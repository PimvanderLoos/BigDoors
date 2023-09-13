package net.minecraft.server;

public abstract class BlockStemmed extends Block {

    public BlockStemmed(Block.Info block_info) {
        super(block_info);
    }

    public abstract BlockStem c();

    public abstract BlockStemAttached d();
}

package net.minecraft.server;

public abstract class BlockDirectional extends Block {

    public static final BlockStateDirection FACING = BlockProperties.H;

    protected BlockDirectional(Block.Info block_info) {
        super(block_info);
    }
}

package net.minecraft.server;

public abstract class BlockDirectional extends Block {

    public static final BlockStateDirection FACING = BlockProperties.M;

    protected BlockDirectional(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }
}

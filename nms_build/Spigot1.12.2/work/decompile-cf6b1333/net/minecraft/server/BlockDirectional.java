package net.minecraft.server;

public abstract class BlockDirectional extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing");

    protected BlockDirectional(Material material) {
        super(material);
    }
}

package net.minecraft.server;

import java.util.function.Supplier;

public abstract class BlockChestAbstract<E extends TileEntity> extends BlockTileEntity {

    protected final Supplier<TileEntityTypes<? extends E>> a;

    protected BlockChestAbstract(Block.Info block_info, Supplier<TileEntityTypes<? extends E>> supplier) {
        super(block_info);
        this.a = supplier;
    }
}

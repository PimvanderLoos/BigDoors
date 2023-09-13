package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;

public abstract class BlockChestAbstract<E extends TileEntity> extends BlockTileEntity {

    protected final Supplier<TileEntityTypes<? extends E>> a;

    protected BlockChestAbstract(BlockBase.Info blockbase_info, Supplier<TileEntityTypes<? extends E>> supplier) {
        super(blockbase_info);
        this.a = supplier;
    }
}

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class BlockTileEntity extends Block implements ITileEntity {

    protected BlockTileEntity(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    public boolean triggerEvent(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        super.triggerEvent(iblockdata, world, blockposition, i, j);
        TileEntity tileentity = world.getBlockEntity(blockposition);

        return tileentity == null ? false : tileentity.triggerEvent(i, j);
    }

    @Nullable
    @Override
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        return tileentity instanceof ITileInventory ? (ITileInventory) tileentity : null;
    }

    @Nullable
    protected static <E extends TileEntity, A extends TileEntity> BlockEntityTicker<A> createTickerHelper(TileEntityTypes<A> tileentitytypes, TileEntityTypes<E> tileentitytypes1, BlockEntityTicker<? super E> blockentityticker) {
        return tileentitytypes1 == tileentitytypes ? blockentityticker : null;
    }
}

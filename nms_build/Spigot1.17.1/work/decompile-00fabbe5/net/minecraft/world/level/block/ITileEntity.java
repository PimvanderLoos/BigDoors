package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEventListener;

public interface ITileEntity {

    @Nullable
    TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata);

    @Nullable
    default <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return null;
    }

    @Nullable
    default <T extends TileEntity> GameEventListener a(World world, T t0) {
        return null;
    }
}

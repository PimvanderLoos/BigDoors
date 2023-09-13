package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityEnderPortal extends TileEntity {

    protected TileEntityEnderPortal(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        super(tileentitytypes, blockposition, iblockdata);
    }

    public TileEntityEnderPortal(BlockPosition blockposition, IBlockData iblockdata) {
        this(TileEntityTypes.END_PORTAL, blockposition, iblockdata);
    }

    public boolean shouldRenderFace(EnumDirection enumdirection) {
        return enumdirection.getAxis() == EnumDirection.EnumAxis.Y;
    }
}

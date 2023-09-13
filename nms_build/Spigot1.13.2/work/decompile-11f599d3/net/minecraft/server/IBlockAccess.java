package net.minecraft.server;

import javax.annotation.Nullable;

public interface IBlockAccess {

    @Nullable
    TileEntity getTileEntity(BlockPosition blockposition);

    IBlockData getType(BlockPosition blockposition);

    Fluid getFluid(BlockPosition blockposition);

    default int K() {
        return 15;
    }
}

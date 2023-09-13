package net.minecraft.server;

import javax.annotation.Nullable;

public interface IBlockAccess {

    @Nullable
    TileEntity getTileEntity(BlockPosition blockposition);

    IBlockData getType(BlockPosition blockposition);

    Fluid b(BlockPosition blockposition);

    default int J() {
        return 15;
    }
}

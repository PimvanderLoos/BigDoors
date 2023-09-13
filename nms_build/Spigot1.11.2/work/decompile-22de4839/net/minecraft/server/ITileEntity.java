package net.minecraft.server;

import javax.annotation.Nullable;

public interface ITileEntity {

    @Nullable
    TileEntity a(World world, int i);
}

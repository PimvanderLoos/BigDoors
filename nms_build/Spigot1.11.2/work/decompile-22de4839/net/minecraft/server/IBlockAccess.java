package net.minecraft.server;

import javax.annotation.Nullable;

public interface IBlockAccess {

    @Nullable
    TileEntity getTileEntity(BlockPosition blockposition);

    IBlockData getType(BlockPosition blockposition);

    boolean isEmpty(BlockPosition blockposition);

    int getBlockPower(BlockPosition blockposition, EnumDirection enumdirection);
}

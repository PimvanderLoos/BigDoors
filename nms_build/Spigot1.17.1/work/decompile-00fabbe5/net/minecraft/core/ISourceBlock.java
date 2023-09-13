package net.minecraft.core;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public interface ISourceBlock extends IPosition {

    @Override
    double getX();

    @Override
    double getY();

    @Override
    double getZ();

    BlockPosition getBlockPosition();

    IBlockData getBlockData();

    <T extends TileEntity> T getTileEntity();

    WorldServer getWorld();
}

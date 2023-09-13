package net.minecraft.core;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public interface ISourceBlock extends IPosition {

    @Override
    double x();

    @Override
    double y();

    @Override
    double z();

    BlockPosition getPos();

    IBlockData getBlockState();

    <T extends TileEntity> T getEntity();

    WorldServer getLevel();
}

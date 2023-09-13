package net.minecraft.server;

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

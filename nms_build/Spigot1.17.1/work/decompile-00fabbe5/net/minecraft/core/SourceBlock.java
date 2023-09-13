package net.minecraft.core;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class SourceBlock implements ISourceBlock {

    private final WorldServer level;
    private final BlockPosition pos;

    public SourceBlock(WorldServer worldserver, BlockPosition blockposition) {
        this.level = worldserver;
        this.pos = blockposition;
    }

    @Override
    public WorldServer getWorld() {
        return this.level;
    }

    @Override
    public double getX() {
        return (double) this.pos.getX() + 0.5D;
    }

    @Override
    public double getY() {
        return (double) this.pos.getY() + 0.5D;
    }

    @Override
    public double getZ() {
        return (double) this.pos.getZ() + 0.5D;
    }

    @Override
    public BlockPosition getBlockPosition() {
        return this.pos;
    }

    @Override
    public IBlockData getBlockData() {
        return this.level.getType(this.pos);
    }

    @Override
    public <T extends TileEntity> T getTileEntity() {
        return this.level.getTileEntity(this.pos);
    }
}

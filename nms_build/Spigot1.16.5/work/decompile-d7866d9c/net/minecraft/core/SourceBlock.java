package net.minecraft.core;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class SourceBlock implements ISourceBlock {

    private final WorldServer a;
    private final BlockPosition b;

    public SourceBlock(WorldServer worldserver, BlockPosition blockposition) {
        this.a = worldserver;
        this.b = blockposition;
    }

    @Override
    public WorldServer getWorld() {
        return this.a;
    }

    @Override
    public double getX() {
        return (double) this.b.getX() + 0.5D;
    }

    @Override
    public double getY() {
        return (double) this.b.getY() + 0.5D;
    }

    @Override
    public double getZ() {
        return (double) this.b.getZ() + 0.5D;
    }

    @Override
    public BlockPosition getBlockPosition() {
        return this.b;
    }

    @Override
    public IBlockData getBlockData() {
        return this.a.getType(this.b);
    }

    @Override
    public <T extends TileEntity> T getTileEntity() {
        return this.a.getTileEntity(this.b);
    }
}

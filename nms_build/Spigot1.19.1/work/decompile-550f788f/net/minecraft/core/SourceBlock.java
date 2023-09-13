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
    public WorldServer getLevel() {
        return this.level;
    }

    @Override
    public double x() {
        return (double) this.pos.getX() + 0.5D;
    }

    @Override
    public double y() {
        return (double) this.pos.getY() + 0.5D;
    }

    @Override
    public double z() {
        return (double) this.pos.getZ() + 0.5D;
    }

    @Override
    public BlockPosition getPos() {
        return this.pos;
    }

    @Override
    public IBlockData getBlockState() {
        return this.level.getBlockState(this.pos);
    }

    @Override
    public <T extends TileEntity> T getEntity() {
        return this.level.getBlockEntity(this.pos);
    }
}

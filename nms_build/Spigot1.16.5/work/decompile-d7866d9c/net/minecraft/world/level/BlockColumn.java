package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public final class BlockColumn implements IBlockAccess {

    private final IBlockData[] a;

    public BlockColumn(IBlockData[] aiblockdata) {
        this.a = aiblockdata;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return null;
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        int i = blockposition.getY();

        return i >= 0 && i < this.a.length ? this.a[i] : Blocks.AIR.getBlockData();
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return this.getType(blockposition).getFluid();
    }
}

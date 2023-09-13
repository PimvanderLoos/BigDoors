package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;

public enum BlockAccessAir implements IBlockAccess {

    INSTANCE;

    private BlockAccessAir() {}

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        return null;
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        return FluidTypes.EMPTY.defaultFluidState();
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}

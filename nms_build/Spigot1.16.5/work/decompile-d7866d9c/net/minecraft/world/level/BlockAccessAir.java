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
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return null;
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        return Blocks.AIR.getBlockData();
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return FluidTypes.EMPTY.h();
    }
}

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public interface IBlockWaterlogged extends IFluidSource, IFluidContainer {

    @Override
    default boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockProperties.C) && fluidtype == FluidTypes.WATER;
    }

    @Override
    default boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockProperties.C) && fluid.getType() == FluidTypes.WATER) {
            if (!generatoraccess.s_()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.C, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.getType(), fluid.getType().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    default FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockProperties.C)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.C, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }
}

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;

public interface IFluidSource {

    FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata);
}

package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        return !(Boolean) iblockdata.get(BlockProperties.WATERLOGGED) && fluidtype == FluidTypes.WATER;
    }

    @Override
    default boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockProperties.WATERLOGGED) && fluid.getType() == FluidTypes.WATER) {
            if (!generatoraccess.isClientSide()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.WATERLOGGED, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.getType(), fluid.getType().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    default ItemStack removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockProperties.WATERLOGGED)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.WATERLOGGED, false), 3);
            if (!iblockdata.canPlace(generatoraccess, blockposition)) {
                generatoraccess.b(blockposition, true);
            }

            return new ItemStack(Items.WATER_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    default Optional<SoundEffect> V_() {
        return FluidTypes.WATER.k();
    }
}

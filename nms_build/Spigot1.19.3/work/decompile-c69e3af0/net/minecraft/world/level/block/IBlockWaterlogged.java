package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public interface IBlockWaterlogged extends IFluidSource, IFluidContainer {

    @Override
    default boolean canPlaceLiquid(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return fluidtype == FluidTypes.WATER;
    }

    @Override
    default boolean placeLiquid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.getValue(BlockProperties.WATERLOGGED) && fluid.getType() == FluidTypes.WATER) {
            if (!generatoraccess.isClientSide()) {
                generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, true), 3);
                generatoraccess.scheduleTick(blockposition, fluid.getType(), fluid.getType().getTickDelay(generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    default ItemStack pickupBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.getValue(BlockProperties.WATERLOGGED)) {
            generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, false), 3);
            if (!iblockdata.canSurvive(generatoraccess, blockposition)) {
                generatoraccess.destroyBlock(blockposition, true);
            }

            return new ItemStack(Items.WATER_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    default Optional<SoundEffect> getPickupSound() {
        return FluidTypes.WATER.getPickupSound();
    }
}

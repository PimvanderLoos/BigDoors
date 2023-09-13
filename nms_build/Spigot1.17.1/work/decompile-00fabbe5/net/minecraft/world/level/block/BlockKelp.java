package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockKelp extends BlockGrowingTop implements IFluidContainer {

    protected static final VoxelShape SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    private static final double GROW_PER_TICK_PROBABILITY = 0.14D;

    protected BlockKelp(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.UP, BlockKelp.SHAPE, true, 0.14D);
    }

    @Override
    protected boolean g(IBlockData iblockdata) {
        return iblockdata.a(Blocks.WATER);
    }

    @Override
    protected Block c() {
        return Blocks.KELP_PLANT;
    }

    @Override
    protected boolean h(IBlockData iblockdata) {
        return !iblockdata.a(Blocks.MAGMA_BLOCK);
    }

    @Override
    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    @Override
    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }

    @Override
    protected int a(Random random) {
        return 1;
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        return fluid.a((Tag) TagsFluid.WATER) && fluid.e() == 8 ? super.getPlacedState(blockactioncontext) : null;
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }
}

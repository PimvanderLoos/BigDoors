package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;

public class BlockEndRod extends RodBlock {

    protected BlockEndRod(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockEndRod.FACING, EnumDirection.UP));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().relative(enumdirection.getOpposite()));

        return iblockdata.is((Block) this) && iblockdata.getValue(BlockEndRod.FACING) == enumdirection ? (IBlockData) this.defaultBlockState().setValue(BlockEndRod.FACING, enumdirection.getOpposite()) : (IBlockData) this.defaultBlockState().setValue(BlockEndRod.FACING, enumdirection);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockEndRod.FACING);
        double d0 = (double) blockposition.getX() + 0.55D - (double) (random.nextFloat() * 0.1F);
        double d1 = (double) blockposition.getY() + 0.55D - (double) (random.nextFloat() * 0.1F);
        double d2 = (double) blockposition.getZ() + 0.55D - (double) (random.nextFloat() * 0.1F);
        double d3 = (double) (0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);

        if (random.nextInt(5) == 0) {
            world.addParticle(Particles.END_ROD, d0 + (double) enumdirection.getStepX() * d3, d1 + (double) enumdirection.getStepY() * d3, d2 + (double) enumdirection.getStepZ() * d3, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
        }

    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockEndRod.FACING);
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }
}

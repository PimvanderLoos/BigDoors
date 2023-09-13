package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockLantern extends Block implements IBlockWaterlogged {

    public static final BlockStateBoolean HANGING = BlockProperties.HANGING;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final VoxelShape AABB = VoxelShapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
    protected static final VoxelShape HANGING_AABB = VoxelShapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));

    public BlockLantern(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockLantern.HANGING, false)).setValue(BlockLantern.WATERLOGGED, false));
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        EnumDirection[] aenumdirection = blockactioncontext.getNearestLookingDirections();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.getAxis() == EnumDirection.EnumAxis.Y) {
                IBlockData iblockdata = (IBlockData) this.defaultBlockState().setValue(BlockLantern.HANGING, enumdirection == EnumDirection.UP);

                if (iblockdata.canSurvive(blockactioncontext.getLevel(), blockactioncontext.getClickedPos())) {
                    return (IBlockData) iblockdata.setValue(BlockLantern.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
                }
            }
        }

        return null;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Boolean) iblockdata.getValue(BlockLantern.HANGING) ? BlockLantern.HANGING_AABB : BlockLantern.AABB;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockLantern.HANGING, BlockLantern.WATERLOGGED);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = getConnectedDirection(iblockdata).getOpposite();

        return Block.canSupportCenter(iworldreader, blockposition.relative(enumdirection), enumdirection.getOpposite());
    }

    protected static EnumDirection getConnectedDirection(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockLantern.HANGING) ? EnumDirection.DOWN : EnumDirection.UP;
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockLantern.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return getConnectedDirection(iblockdata).getOpposite() == enumdirection && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockLantern.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertySlabType;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockStepAbstract extends Block implements IBlockWaterlogged {

    public static final BlockStateEnum<BlockPropertySlabType> TYPE = BlockProperties.SLAB_TYPE;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockStepAbstract(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockStepAbstract.TYPE, BlockPropertySlabType.BOTTOM)).setValue(BlockStepAbstract.WATERLOGGED, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return iblockdata.getValue(BlockStepAbstract.TYPE) != BlockPropertySlabType.DOUBLE;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockStepAbstract.TYPE, BlockStepAbstract.WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        BlockPropertySlabType blockpropertyslabtype = (BlockPropertySlabType) iblockdata.getValue(BlockStepAbstract.TYPE);

        switch (blockpropertyslabtype) {
            case DOUBLE:
                return VoxelShapes.block();
            case TOP:
                return BlockStepAbstract.TOP_AABB;
            default:
                return BlockStepAbstract.BOTTOM_AABB;
        }
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockposition);

        if (iblockdata.is((Block) this)) {
            return (IBlockData) ((IBlockData) iblockdata.setValue(BlockStepAbstract.TYPE, BlockPropertySlabType.DOUBLE)).setValue(BlockStepAbstract.WATERLOGGED, false);
        } else {
            Fluid fluid = blockactioncontext.getLevel().getFluidState(blockposition);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockStepAbstract.TYPE, BlockPropertySlabType.BOTTOM)).setValue(BlockStepAbstract.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
            EnumDirection enumdirection = blockactioncontext.getClickedFace();

            return enumdirection != EnumDirection.DOWN && (enumdirection == EnumDirection.UP || blockactioncontext.getClickLocation().y - (double) blockposition.getY() <= 0.5D) ? iblockdata1 : (IBlockData) iblockdata1.setValue(BlockStepAbstract.TYPE, BlockPropertySlabType.TOP);
        }
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        ItemStack itemstack = blockactioncontext.getItemInHand();
        BlockPropertySlabType blockpropertyslabtype = (BlockPropertySlabType) iblockdata.getValue(BlockStepAbstract.TYPE);

        if (blockpropertyslabtype != BlockPropertySlabType.DOUBLE && itemstack.is(this.asItem())) {
            if (blockactioncontext.replacingClickedOnBlock()) {
                boolean flag = blockactioncontext.getClickLocation().y - (double) blockactioncontext.getClickedPos().getY() > 0.5D;
                EnumDirection enumdirection = blockactioncontext.getClickedFace();

                return blockpropertyslabtype == BlockPropertySlabType.BOTTOM ? enumdirection == EnumDirection.UP || flag && enumdirection.getAxis().isHorizontal() : enumdirection == EnumDirection.DOWN || !flag && enumdirection.getAxis().isHorizontal();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockStepAbstract.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public boolean placeLiquid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return iblockdata.getValue(BlockStepAbstract.TYPE) != BlockPropertySlabType.DOUBLE ? IBlockWaterlogged.super.placeLiquid(generatoraccess, blockposition, iblockdata, fluid) : false;
    }

    @Override
    public boolean canPlaceLiquid(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return iblockdata.getValue(BlockStepAbstract.TYPE) != BlockPropertySlabType.DOUBLE ? IBlockWaterlogged.super.canPlaceLiquid(iblockaccess, blockposition, iblockdata, fluidtype) : false;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockStepAbstract.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return false;
            case WATER:
                return iblockaccess.getFluidState(blockposition).is(TagsFluid.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }
}

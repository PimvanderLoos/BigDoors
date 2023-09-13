package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.grower.MangroveTreeGrower;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class MangrovePropaguleBlock extends BlockSapling implements IBlockWaterlogged {

    public static final BlockStateInteger AGE = BlockProperties.AGE_4;
    public static final int MAX_AGE = 4;
    private static final VoxelShape[] SHAPE_PER_AGE = new VoxelShape[]{Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 7.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 3.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateBoolean HANGING = BlockProperties.HANGING;
    private static final float GROW_TALL_MANGROVE_PROBABILITY = 0.85F;

    public MangrovePropaguleBlock(BlockBase.Info blockbase_info) {
        super(new MangroveTreeGrower(0.85F), blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(MangrovePropaguleBlock.STAGE, 0)).setValue(MangrovePropaguleBlock.AGE, 0)).setValue(MangrovePropaguleBlock.WATERLOGGED, false)).setValue(MangrovePropaguleBlock.HANGING, false));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(MangrovePropaguleBlock.STAGE).add(MangrovePropaguleBlock.AGE).add(MangrovePropaguleBlock.WATERLOGGED).add(MangrovePropaguleBlock.HANGING);
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return super.mayPlaceOn(iblockdata, iblockaccess, blockposition) || iblockdata.is(Blocks.CLAY);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        boolean flag = fluid.getType() == FluidTypes.WATER;

        return (IBlockData) ((IBlockData) super.getStateForPlacement(blockactioncontext).setValue(MangrovePropaguleBlock.WATERLOGGED, flag)).setValue(MangrovePropaguleBlock.AGE, 4);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        Vec3D vec3d = iblockdata.getOffset(iblockaccess, blockposition);
        VoxelShape voxelshape;

        if (!(Boolean) iblockdata.getValue(MangrovePropaguleBlock.HANGING)) {
            voxelshape = MangrovePropaguleBlock.SHAPE_PER_AGE[4];
        } else {
            voxelshape = MangrovePropaguleBlock.SHAPE_PER_AGE[(Integer) iblockdata.getValue(MangrovePropaguleBlock.AGE)];
        }

        return voxelshape.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return isHanging(iblockdata) ? iworldreader.getBlockState(blockposition.above()).is(Blocks.MANGROVE_LEAVES) : super.canSurvive(iblockdata, iworldreader, blockposition);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(MangrovePropaguleBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return enumdirection == EnumDirection.UP && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(MangrovePropaguleBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!isHanging(iblockdata)) {
            if (randomsource.nextInt(7) == 0) {
                this.advanceTree(worldserver, blockposition, iblockdata, randomsource);
            }

        } else {
            if (!isFullyGrown(iblockdata)) {
                worldserver.setBlock(blockposition, (IBlockData) iblockdata.cycle(MangrovePropaguleBlock.AGE), 2);
            }

        }
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !isHanging(iblockdata) || !isFullyGrown(iblockdata);
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return isHanging(iblockdata) ? !isFullyGrown(iblockdata) : super.isBonemealSuccess(world, randomsource, blockposition, iblockdata);
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        if (isHanging(iblockdata) && !isFullyGrown(iblockdata)) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.cycle(MangrovePropaguleBlock.AGE), 2);
        } else {
            super.performBonemeal(worldserver, randomsource, blockposition, iblockdata);
        }

    }

    private static boolean isHanging(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(MangrovePropaguleBlock.HANGING);
    }

    private static boolean isFullyGrown(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(MangrovePropaguleBlock.AGE) == 4;
    }

    public static IBlockData createNewHangingPropagule() {
        return createNewHangingPropagule(0);
    }

    public static IBlockData createNewHangingPropagule(int i) {
        return (IBlockData) ((IBlockData) Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)).setValue(MangrovePropaguleBlock.AGE, i);
    }
}

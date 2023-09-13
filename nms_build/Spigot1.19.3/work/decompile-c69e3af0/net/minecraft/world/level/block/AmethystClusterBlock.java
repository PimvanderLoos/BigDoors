package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class AmethystClusterBlock extends AmethystBlock implements IBlockWaterlogged {

    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateDirection FACING = BlockProperties.FACING;
    protected final VoxelShape northAabb;
    protected final VoxelShape southAabb;
    protected final VoxelShape eastAabb;
    protected final VoxelShape westAabb;
    protected final VoxelShape upAabb;
    protected final VoxelShape downAabb;

    public AmethystClusterBlock(int i, int j, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.defaultBlockState().setValue(AmethystClusterBlock.WATERLOGGED, false)).setValue(AmethystClusterBlock.FACING, EnumDirection.UP));
        this.upAabb = Block.box((double) j, 0.0D, (double) j, (double) (16 - j), (double) i, (double) (16 - j));
        this.downAabb = Block.box((double) j, (double) (16 - i), (double) j, (double) (16 - j), 16.0D, (double) (16 - j));
        this.northAabb = Block.box((double) j, (double) j, (double) (16 - i), (double) (16 - j), (double) (16 - j), 16.0D);
        this.southAabb = Block.box((double) j, (double) j, 0.0D, (double) (16 - j), (double) (16 - j), (double) i);
        this.eastAabb = Block.box(0.0D, (double) j, (double) j, (double) i, (double) (16 - j), (double) (16 - j));
        this.westAabb = Block.box((double) (16 - i), (double) j, (double) j, 16.0D, (double) (16 - j), (double) (16 - j));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(AmethystClusterBlock.FACING);

        switch (enumdirection) {
            case NORTH:
                return this.northAabb;
            case SOUTH:
                return this.southAabb;
            case EAST:
                return this.eastAabb;
            case WEST:
                return this.westAabb;
            case DOWN:
                return this.downAabb;
            case UP:
            default:
                return this.upAabb;
        }
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(AmethystClusterBlock.FACING);
        BlockPosition blockposition1 = blockposition.relative(enumdirection.getOpposite());

        return iworldreader.getBlockState(blockposition1).isFaceSturdy(iworldreader, blockposition1, enumdirection);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(AmethystClusterBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return enumdirection == ((EnumDirection) iblockdata.getValue(AmethystClusterBlock.FACING)).getOpposite() && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();

        return (IBlockData) ((IBlockData) this.defaultBlockState().setValue(AmethystClusterBlock.WATERLOGGED, world.getFluidState(blockposition).getType() == FluidTypes.WATER)).setValue(AmethystClusterBlock.FACING, blockactioncontext.getClickedFace());
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(AmethystClusterBlock.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(AmethystClusterBlock.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(AmethystClusterBlock.FACING)));
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(AmethystClusterBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(AmethystClusterBlock.WATERLOGGED, AmethystClusterBlock.FACING);
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }
}

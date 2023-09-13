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
        this.k((IBlockData) ((IBlockData) this.getBlockData().set(AmethystClusterBlock.WATERLOGGED, false)).set(AmethystClusterBlock.FACING, EnumDirection.UP));
        this.upAabb = Block.a((double) j, 0.0D, (double) j, (double) (16 - j), (double) i, (double) (16 - j));
        this.downAabb = Block.a((double) j, (double) (16 - i), (double) j, (double) (16 - j), 16.0D, (double) (16 - j));
        this.northAabb = Block.a((double) j, (double) j, (double) (16 - i), (double) (16 - j), (double) (16 - j), 16.0D);
        this.southAabb = Block.a((double) j, (double) j, 0.0D, (double) (16 - j), (double) (16 - j), (double) i);
        this.eastAabb = Block.a(0.0D, (double) j, (double) j, (double) i, (double) (16 - j), (double) (16 - j));
        this.westAabb = Block.a((double) (16 - i), (double) j, (double) j, 16.0D, (double) (16 - j), (double) (16 - j));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(AmethystClusterBlock.FACING);

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
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(AmethystClusterBlock.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

        return iworldreader.getType(blockposition1).d(iworldreader, blockposition1, enumdirection);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(AmethystClusterBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection == ((EnumDirection) iblockdata.get(AmethystClusterBlock.FACING)).opposite() && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return (IBlockData) ((IBlockData) this.getBlockData().set(AmethystClusterBlock.WATERLOGGED, world.getFluid(blockposition).getType() == FluidTypes.WATER)).set(AmethystClusterBlock.FACING, blockactioncontext.getClickedFace());
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(AmethystClusterBlock.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(AmethystClusterBlock.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(AmethystClusterBlock.FACING)));
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(AmethystClusterBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(AmethystClusterBlock.WATERLOGGED, AmethystClusterBlock.FACING);
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }
}

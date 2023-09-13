package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyHalf;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockTrapdoor extends BlockFacingHorizontal implements IBlockWaterlogged {

    public static final BlockStateBoolean OPEN = BlockProperties.OPEN;
    public static final BlockStateEnum<BlockPropertyHalf> HALF = BlockProperties.HALF;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final int AABB_THICKNESS = 3;
    protected static final VoxelShape EAST_OPEN_AABB = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_OPEN_AABB = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape BOTTOM_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape TOP_AABB = Block.a(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    protected BlockTrapdoor(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockTrapdoor.FACING, EnumDirection.NORTH)).set(BlockTrapdoor.OPEN, false)).set(BlockTrapdoor.HALF, BlockPropertyHalf.BOTTOM)).set(BlockTrapdoor.POWERED, false)).set(BlockTrapdoor.WATERLOGGED, false));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if (!(Boolean) iblockdata.get(BlockTrapdoor.OPEN)) {
            return iblockdata.get(BlockTrapdoor.HALF) == BlockPropertyHalf.TOP ? BlockTrapdoor.TOP_AABB : BlockTrapdoor.BOTTOM_AABB;
        } else {
            switch ((EnumDirection) iblockdata.get(BlockTrapdoor.FACING)) {
                case NORTH:
                default:
                    return BlockTrapdoor.NORTH_OPEN_AABB;
                case SOUTH:
                    return BlockTrapdoor.SOUTH_OPEN_AABB;
                case WEST:
                    return BlockTrapdoor.WEST_OPEN_AABB;
                case EAST:
                    return BlockTrapdoor.EAST_OPEN_AABB;
            }
        }
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return (Boolean) iblockdata.get(BlockTrapdoor.OPEN);
            case WATER:
                return (Boolean) iblockdata.get(BlockTrapdoor.WATERLOGGED);
            case AIR:
                return (Boolean) iblockdata.get(BlockTrapdoor.OPEN);
            default:
                return false;
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (this.material == Material.METAL) {
            return EnumInteractionResult.PASS;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockTrapdoor.OPEN);
            world.setTypeAndData(blockposition, iblockdata, 2);
            if ((Boolean) iblockdata.get(BlockTrapdoor.WATERLOGGED)) {
                world.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) world));
            }

            this.a(entityhuman, world, blockposition, (Boolean) iblockdata.get(BlockTrapdoor.OPEN));
            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    protected void a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, boolean flag) {
        int i;

        if (flag) {
            i = this.material == Material.METAL ? 1037 : 1007;
            world.a(entityhuman, i, blockposition, 0);
        } else {
            i = this.material == Material.METAL ? 1036 : 1013;
            world.a(entityhuman, i, blockposition, 0);
        }

        world.a((Entity) entityhuman, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockposition);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            boolean flag1 = world.isBlockIndirectlyPowered(blockposition);

            if (flag1 != (Boolean) iblockdata.get(BlockTrapdoor.POWERED)) {
                if ((Boolean) iblockdata.get(BlockTrapdoor.OPEN) != flag1) {
                    iblockdata = (IBlockData) iblockdata.set(BlockTrapdoor.OPEN, flag1);
                    this.a((EntityHuman) null, world, blockposition, flag1);
                }

                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTrapdoor.POWERED, flag1), 2);
                if ((Boolean) iblockdata.get(BlockTrapdoor.WATERLOGGED)) {
                    world.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) world));
                }
            }

        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlockData();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        EnumDirection enumdirection = blockactioncontext.getClickedFace();

        if (!blockactioncontext.c() && enumdirection.n().d()) {
            iblockdata = (IBlockData) ((IBlockData) iblockdata.set(BlockTrapdoor.FACING, enumdirection)).set(BlockTrapdoor.HALF, blockactioncontext.getPos().y - (double) blockactioncontext.getClickPosition().getY() > 0.5D ? BlockPropertyHalf.TOP : BlockPropertyHalf.BOTTOM);
        } else {
            iblockdata = (IBlockData) ((IBlockData) iblockdata.set(BlockTrapdoor.FACING, blockactioncontext.g().opposite())).set(BlockTrapdoor.HALF, enumdirection == EnumDirection.UP ? BlockPropertyHalf.BOTTOM : BlockPropertyHalf.TOP);
        }

        if (blockactioncontext.getWorld().isBlockIndirectlyPowered(blockactioncontext.getClickPosition())) {
            iblockdata = (IBlockData) ((IBlockData) iblockdata.set(BlockTrapdoor.OPEN, true)).set(BlockTrapdoor.POWERED, true);
        }

        return (IBlockData) iblockdata.set(BlockTrapdoor.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTrapdoor.FACING, BlockTrapdoor.OPEN, BlockTrapdoor.HALF, BlockTrapdoor.POWERED, BlockTrapdoor.WATERLOGGED);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockTrapdoor.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockTrapdoor.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }
}

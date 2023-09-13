package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockMinecartTrackAbstract extends Block implements IBlockWaterlogged {

    protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private final boolean isStraight;

    public static boolean isRail(World world, BlockPosition blockposition) {
        return isRail(world.getBlockState(blockposition));
    }

    public static boolean isRail(IBlockData iblockdata) {
        return iblockdata.is((Tag) TagsBlock.RAILS) && iblockdata.getBlock() instanceof BlockMinecartTrackAbstract;
    }

    protected BlockMinecartTrackAbstract(boolean flag, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.isStraight = flag;
    }

    public boolean isStraight() {
        return this.isStraight;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.is((Block) this) ? (BlockPropertyTrackPosition) iblockdata.getValue(this.getShapeProperty()) : null;

        return blockpropertytrackposition != null && blockpropertytrackposition.isAscending() ? BlockMinecartTrackAbstract.HALF_BLOCK_AABB : BlockMinecartTrackAbstract.FLAT_AABB;
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return canSupportRigidBlock(iworldreader, blockposition.below());
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            this.updateState(iblockdata, world, blockposition, flag);
        }
    }

    protected IBlockData updateState(IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        iblockdata = this.updateDir(world, blockposition, iblockdata, true);
        if (this.isStraight) {
            iblockdata.neighborChanged(world, blockposition, this, blockposition, flag);
        }

        return iblockdata;
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide && world.getBlockState(blockposition).is((Block) this)) {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(this.getShapeProperty());

            if (shouldBeRemoved(blockposition, world, blockpropertytrackposition)) {
                dropResources(iblockdata, world, blockposition);
                world.removeBlock(blockposition, flag);
            } else {
                this.updateState(iblockdata, world, blockposition, block);
            }

        }
    }

    private static boolean shouldBeRemoved(BlockPosition blockposition, World world, BlockPropertyTrackPosition blockpropertytrackposition) {
        if (!canSupportRigidBlock(world, blockposition.below())) {
            return true;
        } else {
            switch (blockpropertytrackposition) {
                case ASCENDING_EAST:
                    return !canSupportRigidBlock(world, blockposition.east());
                case ASCENDING_WEST:
                    return !canSupportRigidBlock(world, blockposition.west());
                case ASCENDING_NORTH:
                    return !canSupportRigidBlock(world, blockposition.north());
                case ASCENDING_SOUTH:
                    return !canSupportRigidBlock(world, blockposition.south());
                default:
                    return false;
            }
        }
    }

    protected void updateState(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {}

    protected IBlockData updateDir(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        if (world.isClientSide) {
            return iblockdata;
        } else {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(this.getShapeProperty());

            return (new MinecartTrackLogic(world, blockposition, iblockdata)).place(world.hasNeighborSignal(blockposition), flag, blockpropertytrackposition).getState();
        }
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag) {
            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
            if (((BlockPropertyTrackPosition) iblockdata.getValue(this.getShapeProperty())).isAscending()) {
                world.updateNeighborsAt(blockposition.above(), this);
            }

            if (this.isStraight) {
                world.updateNeighborsAt(blockposition, this);
                world.updateNeighborsAt(blockposition.below(), this);
            }

        }
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        boolean flag = fluid.getType() == FluidTypes.WATER;
        IBlockData iblockdata = super.defaultBlockState();
        EnumDirection enumdirection = blockactioncontext.getHorizontalDirection();
        boolean flag1 = enumdirection == EnumDirection.EAST || enumdirection == EnumDirection.WEST;

        return (IBlockData) ((IBlockData) iblockdata.setValue(this.getShapeProperty(), flag1 ? BlockPropertyTrackPosition.EAST_WEST : BlockPropertyTrackPosition.NORTH_SOUTH)).setValue(BlockMinecartTrackAbstract.WATERLOGGED, flag);
    }

    public abstract IBlockState<BlockPropertyTrackPosition> getShapeProperty();

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockMinecartTrackAbstract.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockMinecartTrackAbstract.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }
}

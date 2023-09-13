package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class BlockPoweredRail extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockPropertyTrackPosition> SHAPE = BlockProperties.RAIL_SHAPE_STRAIGHT;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;

    protected BlockPoweredRail(BlockBase.Info blockbase_info) {
        super(true, blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH)).setValue(BlockPoweredRail.POWERED, false)).setValue(BlockPoweredRail.WATERLOGGED, false));
    }

    protected boolean findPoweredRailSignal(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag, int i) {
        if (i >= 8) {
            return false;
        } else {
            int j = blockposition.getX();
            int k = blockposition.getY();
            int l = blockposition.getZ();
            boolean flag1 = true;
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE);

            switch (blockpropertytrackposition) {
                case NORTH_SOUTH:
                    if (flag) {
                        ++l;
                    } else {
                        --l;
                    }
                    break;
                case EAST_WEST:
                    if (flag) {
                        --j;
                    } else {
                        ++j;
                    }
                    break;
                case ASCENDING_EAST:
                    if (flag) {
                        --j;
                    } else {
                        ++j;
                        ++k;
                        flag1 = false;
                    }

                    blockpropertytrackposition = BlockPropertyTrackPosition.EAST_WEST;
                    break;
                case ASCENDING_WEST:
                    if (flag) {
                        --j;
                        ++k;
                        flag1 = false;
                    } else {
                        ++j;
                    }

                    blockpropertytrackposition = BlockPropertyTrackPosition.EAST_WEST;
                    break;
                case ASCENDING_NORTH:
                    if (flag) {
                        ++l;
                    } else {
                        --l;
                        ++k;
                        flag1 = false;
                    }

                    blockpropertytrackposition = BlockPropertyTrackPosition.NORTH_SOUTH;
                    break;
                case ASCENDING_SOUTH:
                    if (flag) {
                        ++l;
                        ++k;
                        flag1 = false;
                    } else {
                        --l;
                    }

                    blockpropertytrackposition = BlockPropertyTrackPosition.NORTH_SOUTH;
            }

            return this.isSameRailWithPower(world, new BlockPosition(j, k, l), flag, i, blockpropertytrackposition) ? true : flag1 && this.isSameRailWithPower(world, new BlockPosition(j, k - 1, l), flag, i, blockpropertytrackposition);
        }
    }

    protected boolean isSameRailWithPower(World world, BlockPosition blockposition, boolean flag, int i, BlockPropertyTrackPosition blockpropertytrackposition) {
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (!iblockdata.is((Block) this)) {
            return false;
        } else {
            BlockPropertyTrackPosition blockpropertytrackposition1 = (BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE);

            return blockpropertytrackposition == BlockPropertyTrackPosition.EAST_WEST && (blockpropertytrackposition1 == BlockPropertyTrackPosition.NORTH_SOUTH || blockpropertytrackposition1 == BlockPropertyTrackPosition.ASCENDING_NORTH || blockpropertytrackposition1 == BlockPropertyTrackPosition.ASCENDING_SOUTH) ? false : (blockpropertytrackposition == BlockPropertyTrackPosition.NORTH_SOUTH && (blockpropertytrackposition1 == BlockPropertyTrackPosition.EAST_WEST || blockpropertytrackposition1 == BlockPropertyTrackPosition.ASCENDING_EAST || blockpropertytrackposition1 == BlockPropertyTrackPosition.ASCENDING_WEST) ? false : ((Boolean) iblockdata.getValue(BlockPoweredRail.POWERED) ? (world.hasNeighborSignal(blockposition) ? true : this.findPoweredRailSignal(world, blockposition, iblockdata, flag, i + 1)) : false));
        }
    }

    @Override
    protected void updateState(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
        boolean flag = (Boolean) iblockdata.getValue(BlockPoweredRail.POWERED);
        boolean flag1 = world.hasNeighborSignal(blockposition) || this.findPoweredRailSignal(world, blockposition, iblockdata, true, 0) || this.findPoweredRailSignal(world, blockposition, iblockdata, false, 0);

        if (flag1 != flag) {
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockPoweredRail.POWERED, flag1), 3);
            world.updateNeighborsAt(blockposition.below(), this);
            if (((BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE)).isAscending()) {
                world.updateNeighborsAt(blockposition.above(), this);
            }
        }

    }

    @Override
    public IBlockState<BlockPropertyTrackPosition> getShapeProperty() {
        return BlockPoweredRail.SHAPE;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                }
            case COUNTERCLOCKWISE_90:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE)) {
                    case NORTH_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
                    case EAST_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                }
            case CLOCKWISE_90:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE)) {
                    case NORTH_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
                    case EAST_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                }
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(BlockPoweredRail.SHAPE);

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                switch (blockpropertytrackposition) {
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    default:
                        return super.mirror(iblockdata, enumblockmirror);
                }
            case FRONT_BACK:
                switch (blockpropertytrackposition) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockPoweredRail.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                }
        }

        return super.mirror(iblockdata, enumblockmirror);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPoweredRail.SHAPE, BlockPoweredRail.POWERED, BlockPoweredRail.WATERLOGGED);
    }
}

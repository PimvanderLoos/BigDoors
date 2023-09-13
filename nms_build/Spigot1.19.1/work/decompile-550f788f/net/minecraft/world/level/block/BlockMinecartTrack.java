package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class BlockMinecartTrack extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockPropertyTrackPosition> SHAPE = BlockProperties.RAIL_SHAPE;

    protected BlockMinecartTrack(BlockBase.Info blockbase_info) {
        super(false, blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH)).setValue(BlockMinecartTrack.WATERLOGGED, false));
    }

    @Override
    protected void updateState(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
        if (block.defaultBlockState().isSignalSource() && (new MinecartTrackLogic(world, blockposition, iblockdata)).countPotentialConnections() == 3) {
            this.updateDir(world, blockposition, iblockdata, false);
        }

    }

    @Override
    public IBlockState<BlockPropertyTrackPosition> getShapeProperty() {
        return BlockMinecartTrack.SHAPE;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartTrack.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                }
            case COUNTERCLOCKWISE_90:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartTrack.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
                    case EAST_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
                }
            case CLOCKWISE_90:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartTrack.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
                    case EAST_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
                }
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartTrack.SHAPE);

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                switch (blockpropertytrackposition) {
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    default:
                        return super.mirror(iblockdata, enumblockmirror);
                }
            case FRONT_BACK:
                switch (blockpropertytrackposition) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                }
        }

        return super.mirror(iblockdata, enumblockmirror);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockMinecartTrack.SHAPE, BlockMinecartTrack.WATERLOGGED);
    }
}

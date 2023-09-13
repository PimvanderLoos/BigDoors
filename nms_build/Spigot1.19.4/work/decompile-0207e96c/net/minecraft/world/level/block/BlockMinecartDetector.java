package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.entity.vehicle.EntityMinecartCommandBlock;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockMinecartDetector extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockPropertyTrackPosition> SHAPE = BlockProperties.RAIL_SHAPE_STRAIGHT;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    private static final int PRESSED_CHECK_PERIOD = 20;

    public BlockMinecartDetector(BlockBase.Info blockbase_info) {
        super(true, blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockMinecartDetector.POWERED, false)).setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH)).setValue(BlockMinecartDetector.WATERLOGGED, false));
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide) {
            if (!(Boolean) iblockdata.getValue(BlockMinecartDetector.POWERED)) {
                this.checkPressed(world, blockposition, iblockdata);
            }
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockMinecartDetector.POWERED)) {
            this.checkPressed(worldserver, blockposition, iblockdata);
        }
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockMinecartDetector.POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !(Boolean) iblockdata.getValue(BlockMinecartDetector.POWERED) ? 0 : (enumdirection == EnumDirection.UP ? 15 : 0);
    }

    private void checkPressed(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.canSurvive(iblockdata, world, blockposition)) {
            boolean flag = (Boolean) iblockdata.getValue(BlockMinecartDetector.POWERED);
            boolean flag1 = false;
            List<EntityMinecartAbstract> list = this.getInteractingMinecartOfType(world, blockposition, EntityMinecartAbstract.class, (entity) -> {
                return true;
            });

            if (!list.isEmpty()) {
                flag1 = true;
            }

            IBlockData iblockdata1;

            if (flag1 && !flag) {
                iblockdata1 = (IBlockData) iblockdata.setValue(BlockMinecartDetector.POWERED, true);
                world.setBlock(blockposition, iblockdata1, 3);
                this.updatePowerToConnected(world, blockposition, iblockdata1, true);
                world.updateNeighborsAt(blockposition, this);
                world.updateNeighborsAt(blockposition.below(), this);
                world.setBlocksDirty(blockposition, iblockdata, iblockdata1);
            }

            if (!flag1 && flag) {
                iblockdata1 = (IBlockData) iblockdata.setValue(BlockMinecartDetector.POWERED, false);
                world.setBlock(blockposition, iblockdata1, 3);
                this.updatePowerToConnected(world, blockposition, iblockdata1, false);
                world.updateNeighborsAt(blockposition, this);
                world.updateNeighborsAt(blockposition.below(), this);
                world.setBlocksDirty(blockposition, iblockdata, iblockdata1);
            }

            if (flag1) {
                world.scheduleTick(blockposition, (Block) this, 20);
            }

            world.updateNeighbourForOutputSignal(blockposition, this);
        }
    }

    protected void updatePowerToConnected(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        MinecartTrackLogic minecarttracklogic = new MinecartTrackLogic(world, blockposition, iblockdata);
        List<BlockPosition> list = minecarttracklogic.getConnections();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            IBlockData iblockdata1 = world.getBlockState(blockposition1);

            world.neighborChanged(iblockdata1, blockposition1, iblockdata1.getBlock(), blockposition, false);
        }

    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            IBlockData iblockdata2 = this.updateState(iblockdata, world, blockposition, flag);

            this.checkPressed(world, blockposition, iblockdata2);
        }
    }

    @Override
    public IBlockState<BlockPropertyTrackPosition> getShapeProperty() {
        return BlockMinecartDetector.SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if ((Boolean) iblockdata.getValue(BlockMinecartDetector.POWERED)) {
            List<EntityMinecartCommandBlock> list = this.getInteractingMinecartOfType(world, blockposition, EntityMinecartCommandBlock.class, (entity) -> {
                return true;
            });

            if (!list.isEmpty()) {
                return ((EntityMinecartCommandBlock) list.get(0)).getCommandBlock().getSuccessCount();
            }

            List<EntityMinecartAbstract> list1 = this.getInteractingMinecartOfType(world, blockposition, EntityMinecartAbstract.class, IEntitySelector.CONTAINER_ENTITY_SELECTOR);

            if (!list1.isEmpty()) {
                return Container.getRedstoneSignalFromContainer((IInventory) list1.get(0));
            }
        }

        return 0;
    }

    private <T extends EntityMinecartAbstract> List<T> getInteractingMinecartOfType(World world, BlockPosition blockposition, Class<T> oclass, Predicate<Entity> predicate) {
        return world.getEntitiesOfClass(oclass, this.getSearchBB(blockposition), predicate);
    }

    private AxisAlignedBB getSearchBB(BlockPosition blockposition) {
        double d0 = 0.2D;

        return new AxisAlignedBB((double) blockposition.getX() + 0.2D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.2D, (double) (blockposition.getX() + 1) - 0.2D, (double) (blockposition.getY() + 1) - 0.2D, (double) (blockposition.getZ() + 1) - 0.2D);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartDetector.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                }
            case COUNTERCLOCKWISE_90:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartDetector.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
                    case EAST_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
                }
            case CLOCKWISE_90:
                switch ((BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartDetector.SHAPE)) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
                    case EAST_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
                }
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(BlockMinecartDetector.SHAPE);

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                switch (blockpropertytrackposition) {
                    case ASCENDING_NORTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    default:
                        return super.mirror(iblockdata, enumblockmirror);
                }
            case FRONT_BACK:
                switch (blockpropertytrackposition) {
                    case ASCENDING_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
                    case NORTH_WEST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
                    case NORTH_EAST:
                        return (IBlockData) iblockdata.setValue(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
                }
        }

        return super.mirror(iblockdata, enumblockmirror);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockMinecartDetector.SHAPE, BlockMinecartDetector.POWERED, BlockMinecartDetector.WATERLOGGED);
    }
}

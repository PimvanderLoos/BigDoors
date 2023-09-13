package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockTripwire extends Block {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateBoolean ATTACHED = BlockProperties.ATTACHED;
    public static final BlockStateBoolean DISARMED = BlockProperties.DISARMED;
    public static final BlockStateBoolean NORTH = BlockSprawling.NORTH;
    public static final BlockStateBoolean EAST = BlockSprawling.EAST;
    public static final BlockStateBoolean SOUTH = BlockSprawling.SOUTH;
    public static final BlockStateBoolean WEST = BlockSprawling.WEST;
    private static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = BlockTall.PROPERTY_BY_DIRECTION;
    protected static final VoxelShape AABB = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
    protected static final VoxelShape NOT_ATTACHED_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final int RECHECK_PERIOD = 10;
    private final BlockTripwireHook hook;

    public BlockTripwire(BlockTripwireHook blocktripwirehook, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockTripwire.POWERED, false)).setValue(BlockTripwire.ATTACHED, false)).setValue(BlockTripwire.DISARMED, false)).setValue(BlockTripwire.NORTH, false)).setValue(BlockTripwire.EAST, false)).setValue(BlockTripwire.SOUTH, false)).setValue(BlockTripwire.WEST, false));
        this.hook = blocktripwirehook;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Boolean) iblockdata.getValue(BlockTripwire.ATTACHED) ? BlockTripwire.AABB : BlockTripwire.NOT_ATTACHED_AABB;
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockTripwire.NORTH, this.shouldConnectTo(world.getBlockState(blockposition.north()), EnumDirection.NORTH))).setValue(BlockTripwire.EAST, this.shouldConnectTo(world.getBlockState(blockposition.east()), EnumDirection.EAST))).setValue(BlockTripwire.SOUTH, this.shouldConnectTo(world.getBlockState(blockposition.south()), EnumDirection.SOUTH))).setValue(BlockTripwire.WEST, this.shouldConnectTo(world.getBlockState(blockposition.west()), EnumDirection.WEST));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.getAxis().isHorizontal() ? (IBlockData) iblockdata.setValue((IBlockState) BlockTripwire.PROPERTY_BY_DIRECTION.get(enumdirection), this.shouldConnectTo(iblockdata1, enumdirection)) : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            this.updateSource(world, blockposition, iblockdata);
        }
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.is(iblockdata1.getBlock())) {
            this.updateSource(world, blockposition, (IBlockData) iblockdata.setValue(BlockTripwire.POWERED, true));
        }
    }

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && !entityhuman.getMainHandItem().isEmpty() && entityhuman.getMainHandItem().is(Items.SHEARS)) {
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockTripwire.DISARMED, true), 4);
            world.gameEvent((Entity) entityhuman, GameEvent.SHEAR, blockposition);
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    private void updateSource(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection[] aenumdirection = new EnumDirection[]{EnumDirection.SOUTH, EnumDirection.WEST};
        int i = aenumdirection.length;
        int j = 0;

        while (j < i) {
            EnumDirection enumdirection = aenumdirection[j];
            int k = 1;

            while (true) {
                if (k < 42) {
                    BlockPosition blockposition1 = blockposition.relative(enumdirection, k);
                    IBlockData iblockdata1 = world.getBlockState(blockposition1);

                    if (iblockdata1.is((Block) this.hook)) {
                        if (iblockdata1.getValue(BlockTripwireHook.FACING) == enumdirection.getOpposite()) {
                            this.hook.calculateState(world, blockposition1, iblockdata1, false, true, k, iblockdata);
                        }
                    } else if (iblockdata1.is((Block) this)) {
                        ++k;
                        continue;
                    }
                }

                ++j;
                break;
            }
        }

    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide) {
            if (!(Boolean) iblockdata.getValue(BlockTripwire.POWERED)) {
                this.checkPressed(world, blockposition);
            }
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) worldserver.getBlockState(blockposition).getValue(BlockTripwire.POWERED)) {
            this.checkPressed(worldserver, blockposition);
        }
    }

    private void checkPressed(World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getBlockState(blockposition);
        boolean flag = (Boolean) iblockdata.getValue(BlockTripwire.POWERED);
        boolean flag1 = false;
        List<? extends Entity> list = world.getEntities((Entity) null, iblockdata.getShape(world, blockposition).bounds().move(blockposition));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!entity.isIgnoringBlockTriggers()) {
                    flag1 = true;
                    break;
                }
            }
        }

        if (flag1 != flag) {
            iblockdata = (IBlockData) iblockdata.setValue(BlockTripwire.POWERED, flag1);
            world.setBlock(blockposition, iblockdata, 3);
            this.updateSource(world, blockposition, iblockdata);
        }

        if (flag1) {
            world.scheduleTick(new BlockPosition(blockposition), (Block) this, 10);
        }

    }

    public boolean shouldConnectTo(IBlockData iblockdata, EnumDirection enumdirection) {
        return iblockdata.is((Block) this.hook) ? iblockdata.getValue(BlockTripwireHook.FACING) == enumdirection.getOpposite() : iblockdata.is((Block) this);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockTripwire.NORTH, (Boolean) iblockdata.getValue(BlockTripwire.SOUTH))).setValue(BlockTripwire.EAST, (Boolean) iblockdata.getValue(BlockTripwire.WEST))).setValue(BlockTripwire.SOUTH, (Boolean) iblockdata.getValue(BlockTripwire.NORTH))).setValue(BlockTripwire.WEST, (Boolean) iblockdata.getValue(BlockTripwire.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockTripwire.NORTH, (Boolean) iblockdata.getValue(BlockTripwire.EAST))).setValue(BlockTripwire.EAST, (Boolean) iblockdata.getValue(BlockTripwire.SOUTH))).setValue(BlockTripwire.SOUTH, (Boolean) iblockdata.getValue(BlockTripwire.WEST))).setValue(BlockTripwire.WEST, (Boolean) iblockdata.getValue(BlockTripwire.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockTripwire.NORTH, (Boolean) iblockdata.getValue(BlockTripwire.WEST))).setValue(BlockTripwire.EAST, (Boolean) iblockdata.getValue(BlockTripwire.NORTH))).setValue(BlockTripwire.SOUTH, (Boolean) iblockdata.getValue(BlockTripwire.EAST))).setValue(BlockTripwire.WEST, (Boolean) iblockdata.getValue(BlockTripwire.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockTripwire.NORTH, (Boolean) iblockdata.getValue(BlockTripwire.SOUTH))).setValue(BlockTripwire.SOUTH, (Boolean) iblockdata.getValue(BlockTripwire.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockTripwire.EAST, (Boolean) iblockdata.getValue(BlockTripwire.WEST))).setValue(BlockTripwire.WEST, (Boolean) iblockdata.getValue(BlockTripwire.EAST));
            default:
                return super.mirror(iblockdata, enumblockmirror);
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockTripwire.POWERED, BlockTripwire.ATTACHED, BlockTripwire.DISARMED, BlockTripwire.NORTH, BlockTripwire.EAST, BlockTripwire.WEST, BlockTripwire.SOUTH);
    }
}

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBell;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyBellAttach;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockBell extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockPropertyBellAttach> ATTACHMENT = BlockProperties.BELL_ATTACHMENT;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    private static final VoxelShape BELL_TOP_SHAPE = Block.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
    private static final VoxelShape BELL_BOTTOM_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
    private static final VoxelShape BELL_SHAPE = VoxelShapes.or(BlockBell.BELL_BOTTOM_SHAPE, BlockBell.BELL_TOP_SHAPE);
    private static final VoxelShape NORTH_SOUTH_BETWEEN = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
    private static final VoxelShape EAST_WEST_BETWEEN = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
    private static final VoxelShape TO_WEST = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
    private static final VoxelShape TO_EAST = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
    private static final VoxelShape TO_NORTH = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
    private static final VoxelShape TO_SOUTH = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
    private static final VoxelShape CEILING_SHAPE = VoxelShapes.or(BlockBell.BELL_SHAPE, Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));
    public static final int EVENT_BELL_RING = 1;

    public BlockBell(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockBell.FACING, EnumDirection.NORTH)).setValue(BlockBell.ATTACHMENT, BlockPropertyBellAttach.FLOOR)).setValue(BlockBell.POWERED, false));
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        boolean flag1 = world.hasNeighborSignal(blockposition);

        if (flag1 != (Boolean) iblockdata.getValue(BlockBell.POWERED)) {
            if (flag1) {
                this.attemptToRing(world, blockposition, (EnumDirection) null);
            }

            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockBell.POWERED, flag1), 3);
        }

    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        Entity entity = iprojectile.getOwner();
        EntityHuman entityhuman = entity instanceof EntityHuman ? (EntityHuman) entity : null;

        this.onHit(world, iblockdata, movingobjectpositionblock, entityhuman, true);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return this.onHit(world, iblockdata, movingobjectpositionblock, entityhuman, true) ? EnumInteractionResult.sidedSuccess(world.isClientSide) : EnumInteractionResult.PASS;
    }

    public boolean onHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, @Nullable EntityHuman entityhuman, boolean flag) {
        EnumDirection enumdirection = movingobjectpositionblock.getDirection();
        BlockPosition blockposition = movingobjectpositionblock.getBlockPos();
        boolean flag1 = !flag || this.isProperHit(iblockdata, enumdirection, movingobjectpositionblock.getLocation().y - (double) blockposition.getY());

        if (flag1) {
            boolean flag2 = this.attemptToRing(entityhuman, world, blockposition, enumdirection);

            if (flag2 && entityhuman != null) {
                entityhuman.awardStat(StatisticList.BELL_RING);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isProperHit(IBlockData iblockdata, EnumDirection enumdirection, double d0) {
        if (enumdirection.getAxis() != EnumDirection.EnumAxis.Y && d0 <= 0.8123999834060669D) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.getValue(BlockBell.FACING);
            BlockPropertyBellAttach blockpropertybellattach = (BlockPropertyBellAttach) iblockdata.getValue(BlockBell.ATTACHMENT);

            switch (blockpropertybellattach) {
                case FLOOR:
                    return enumdirection1.getAxis() == enumdirection.getAxis();
                case SINGLE_WALL:
                case DOUBLE_WALL:
                    return enumdirection1.getAxis() != enumdirection.getAxis();
                case CEILING:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean attemptToRing(World world, BlockPosition blockposition, @Nullable EnumDirection enumdirection) {
        return this.attemptToRing((Entity) null, world, blockposition, enumdirection);
    }

    public boolean attemptToRing(@Nullable Entity entity, World world, BlockPosition blockposition, @Nullable EnumDirection enumdirection) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (!world.isClientSide && tileentity instanceof TileEntityBell) {
            if (enumdirection == null) {
                enumdirection = (EnumDirection) world.getBlockState(blockposition).getValue(BlockBell.FACING);
            }

            ((TileEntityBell) tileentity).onHit(enumdirection);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BELL_BLOCK, SoundCategory.BLOCKS, 2.0F, 1.0F);
            world.gameEvent(entity, GameEvent.RING_BELL, blockposition);
            return true;
        } else {
            return false;
        }
    }

    private VoxelShape getVoxelShape(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockBell.FACING);
        BlockPropertyBellAttach blockpropertybellattach = (BlockPropertyBellAttach) iblockdata.getValue(BlockBell.ATTACHMENT);

        return blockpropertybellattach == BlockPropertyBellAttach.FLOOR ? (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH ? BlockBell.EAST_WEST_FLOOR_SHAPE : BlockBell.NORTH_SOUTH_FLOOR_SHAPE) : (blockpropertybellattach == BlockPropertyBellAttach.CEILING ? BlockBell.CEILING_SHAPE : (blockpropertybellattach == BlockPropertyBellAttach.DOUBLE_WALL ? (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH ? BlockBell.EAST_WEST_BETWEEN : BlockBell.NORTH_SOUTH_BETWEEN) : (enumdirection == EnumDirection.NORTH ? BlockBell.TO_NORTH : (enumdirection == EnumDirection.SOUTH ? BlockBell.TO_SOUTH : (enumdirection == EnumDirection.EAST ? BlockBell.TO_EAST : BlockBell.TO_WEST)))));
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getVoxelShape(iblockdata);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getVoxelShape(iblockdata);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        World world = blockactioncontext.getLevel();
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
        IBlockData iblockdata;

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Y) {
            iblockdata = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockBell.ATTACHMENT, enumdirection == EnumDirection.DOWN ? BlockPropertyBellAttach.CEILING : BlockPropertyBellAttach.FLOOR)).setValue(BlockBell.FACING, blockactioncontext.getHorizontalDirection());
            if (iblockdata.canSurvive(blockactioncontext.getLevel(), blockposition)) {
                return iblockdata;
            }
        } else {
            boolean flag = enumdirection_enumaxis == EnumDirection.EnumAxis.X && world.getBlockState(blockposition.west()).isFaceSturdy(world, blockposition.west(), EnumDirection.EAST) && world.getBlockState(blockposition.east()).isFaceSturdy(world, blockposition.east(), EnumDirection.WEST) || enumdirection_enumaxis == EnumDirection.EnumAxis.Z && world.getBlockState(blockposition.north()).isFaceSturdy(world, blockposition.north(), EnumDirection.SOUTH) && world.getBlockState(blockposition.south()).isFaceSturdy(world, blockposition.south(), EnumDirection.NORTH);

            iblockdata = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockBell.FACING, enumdirection.getOpposite())).setValue(BlockBell.ATTACHMENT, flag ? BlockPropertyBellAttach.DOUBLE_WALL : BlockPropertyBellAttach.SINGLE_WALL);
            if (iblockdata.canSurvive(blockactioncontext.getLevel(), blockactioncontext.getClickedPos())) {
                return iblockdata;
            }

            boolean flag1 = world.getBlockState(blockposition.below()).isFaceSturdy(world, blockposition.below(), EnumDirection.UP);

            iblockdata = (IBlockData) iblockdata.setValue(BlockBell.ATTACHMENT, flag1 ? BlockPropertyBellAttach.FLOOR : BlockPropertyBellAttach.CEILING);
            if (iblockdata.canSurvive(blockactioncontext.getLevel(), blockactioncontext.getClickedPos())) {
                return iblockdata;
            }
        }

        return null;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyBellAttach blockpropertybellattach = (BlockPropertyBellAttach) iblockdata.getValue(BlockBell.ATTACHMENT);
        EnumDirection enumdirection1 = getConnectedDirection(iblockdata).getOpposite();

        if (enumdirection1 == enumdirection && !iblockdata.canSurvive(generatoraccess, blockposition) && blockpropertybellattach != BlockPropertyBellAttach.DOUBLE_WALL) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if (enumdirection.getAxis() == ((EnumDirection) iblockdata.getValue(BlockBell.FACING)).getAxis()) {
                if (blockpropertybellattach == BlockPropertyBellAttach.DOUBLE_WALL && !iblockdata1.isFaceSturdy(generatoraccess, blockposition1, enumdirection)) {
                    return (IBlockData) ((IBlockData) iblockdata.setValue(BlockBell.ATTACHMENT, BlockPropertyBellAttach.SINGLE_WALL)).setValue(BlockBell.FACING, enumdirection.getOpposite());
                }

                if (blockpropertybellattach == BlockPropertyBellAttach.SINGLE_WALL && enumdirection1.getOpposite() == enumdirection && iblockdata1.isFaceSturdy(generatoraccess, blockposition1, (EnumDirection) iblockdata.getValue(BlockBell.FACING))) {
                    return (IBlockData) iblockdata.setValue(BlockBell.ATTACHMENT, BlockPropertyBellAttach.DOUBLE_WALL);
                }
            }

            return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = getConnectedDirection(iblockdata).getOpposite();

        return enumdirection == EnumDirection.UP ? Block.canSupportCenter(iworldreader, blockposition.above(), EnumDirection.DOWN) : BlockAttachable.canAttach(iworldreader, blockposition, enumdirection);
    }

    private static EnumDirection getConnectedDirection(IBlockData iblockdata) {
        switch ((BlockPropertyBellAttach) iblockdata.getValue(BlockBell.ATTACHMENT)) {
            case FLOOR:
                return EnumDirection.UP;
            case CEILING:
                return EnumDirection.DOWN;
            default:
                return ((EnumDirection) iblockdata.getValue(BlockBell.FACING)).getOpposite();
        }
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockBell.FACING, BlockBell.ATTACHMENT, BlockBell.POWERED);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBell(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return createTickerHelper(tileentitytypes, TileEntityTypes.BELL, world.isClientSide ? TileEntityBell::clientTick : TileEntityBell::serverTick);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

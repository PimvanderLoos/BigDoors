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
    private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.a(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.a(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    private static final VoxelShape BELL_TOP_SHAPE = Block.a(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
    private static final VoxelShape BELL_BOTTOM_SHAPE = Block.a(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
    private static final VoxelShape BELL_SHAPE = VoxelShapes.a(BlockBell.BELL_BOTTOM_SHAPE, BlockBell.BELL_TOP_SHAPE);
    private static final VoxelShape NORTH_SOUTH_BETWEEN = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
    private static final VoxelShape EAST_WEST_BETWEEN = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
    private static final VoxelShape TO_WEST = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
    private static final VoxelShape TO_EAST = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
    private static final VoxelShape TO_NORTH = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
    private static final VoxelShape TO_SOUTH = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
    private static final VoxelShape CEILING_SHAPE = VoxelShapes.a(BlockBell.BELL_SHAPE, Block.a(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));
    public static final int EVENT_BELL_RING = 1;

    public BlockBell(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockBell.FACING, EnumDirection.NORTH)).set(BlockBell.ATTACHMENT, BlockPropertyBellAttach.FLOOR)).set(BlockBell.POWERED, false));
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        boolean flag1 = world.isBlockIndirectlyPowered(blockposition);

        if (flag1 != (Boolean) iblockdata.get(BlockBell.POWERED)) {
            if (flag1) {
                this.a(world, blockposition, (EnumDirection) null);
            }

            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBell.POWERED, flag1), 3);
        }

    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        Entity entity = iprojectile.getShooter();
        EntityHuman entityhuman = entity instanceof EntityHuman ? (EntityHuman) entity : null;

        this.a(world, iblockdata, movingobjectpositionblock, entityhuman, true);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return this.a(world, iblockdata, movingobjectpositionblock, entityhuman, true) ? EnumInteractionResult.a(world.isClientSide) : EnumInteractionResult.PASS;
    }

    public boolean a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, @Nullable EntityHuman entityhuman, boolean flag) {
        EnumDirection enumdirection = movingobjectpositionblock.getDirection();
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
        boolean flag1 = !flag || this.a(iblockdata, enumdirection, movingobjectpositionblock.getPos().y - (double) blockposition.getY());

        if (flag1) {
            boolean flag2 = this.a((Entity) entityhuman, world, blockposition, enumdirection);

            if (flag2 && entityhuman != null) {
                entityhuman.a(StatisticList.BELL_RING);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean a(IBlockData iblockdata, EnumDirection enumdirection, double d0) {
        if (enumdirection.n() != EnumDirection.EnumAxis.Y && d0 <= 0.8123999834060669D) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockBell.FACING);
            BlockPropertyBellAttach blockpropertybellattach = (BlockPropertyBellAttach) iblockdata.get(BlockBell.ATTACHMENT);

            switch (blockpropertybellattach) {
                case FLOOR:
                    return enumdirection1.n() == enumdirection.n();
                case SINGLE_WALL:
                case DOUBLE_WALL:
                    return enumdirection1.n() != enumdirection.n();
                case CEILING:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean a(World world, BlockPosition blockposition, @Nullable EnumDirection enumdirection) {
        return this.a((Entity) null, world, blockposition, enumdirection);
    }

    public boolean a(@Nullable Entity entity, World world, BlockPosition blockposition, @Nullable EnumDirection enumdirection) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (!world.isClientSide && tileentity instanceof TileEntityBell) {
            if (enumdirection == null) {
                enumdirection = (EnumDirection) world.getType(blockposition).get(BlockBell.FACING);
            }

            ((TileEntityBell) tileentity).a(enumdirection);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BELL_BLOCK, SoundCategory.BLOCKS, 2.0F, 1.0F);
            world.a(entity, GameEvent.RING_BELL, blockposition);
            return true;
        } else {
            return false;
        }
    }

    private VoxelShape h(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBell.FACING);
        BlockPropertyBellAttach blockpropertybellattach = (BlockPropertyBellAttach) iblockdata.get(BlockBell.ATTACHMENT);

        return blockpropertybellattach == BlockPropertyBellAttach.FLOOR ? (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH ? BlockBell.EAST_WEST_FLOOR_SHAPE : BlockBell.NORTH_SOUTH_FLOOR_SHAPE) : (blockpropertybellattach == BlockPropertyBellAttach.CEILING ? BlockBell.CEILING_SHAPE : (blockpropertybellattach == BlockPropertyBellAttach.DOUBLE_WALL ? (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH ? BlockBell.EAST_WEST_BETWEEN : BlockBell.NORTH_SOUTH_BETWEEN) : (enumdirection == EnumDirection.NORTH ? BlockBell.TO_NORTH : (enumdirection == EnumDirection.SOUTH ? BlockBell.TO_SOUTH : (enumdirection == EnumDirection.EAST ? BlockBell.TO_EAST : BlockBell.TO_WEST)))));
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.h(iblockdata);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.h(iblockdata);
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        World world = blockactioncontext.getWorld();
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();
        IBlockData iblockdata;

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Y) {
            iblockdata = (IBlockData) ((IBlockData) this.getBlockData().set(BlockBell.ATTACHMENT, enumdirection == EnumDirection.DOWN ? BlockPropertyBellAttach.CEILING : BlockPropertyBellAttach.FLOOR)).set(BlockBell.FACING, blockactioncontext.g());
            if (iblockdata.canPlace(blockactioncontext.getWorld(), blockposition)) {
                return iblockdata;
            }
        } else {
            boolean flag = enumdirection_enumaxis == EnumDirection.EnumAxis.X && world.getType(blockposition.west()).d(world, blockposition.west(), EnumDirection.EAST) && world.getType(blockposition.east()).d(world, blockposition.east(), EnumDirection.WEST) || enumdirection_enumaxis == EnumDirection.EnumAxis.Z && world.getType(blockposition.north()).d(world, blockposition.north(), EnumDirection.SOUTH) && world.getType(blockposition.south()).d(world, blockposition.south(), EnumDirection.NORTH);

            iblockdata = (IBlockData) ((IBlockData) this.getBlockData().set(BlockBell.FACING, enumdirection.opposite())).set(BlockBell.ATTACHMENT, flag ? BlockPropertyBellAttach.DOUBLE_WALL : BlockPropertyBellAttach.SINGLE_WALL);
            if (iblockdata.canPlace(blockactioncontext.getWorld(), blockactioncontext.getClickPosition())) {
                return iblockdata;
            }

            boolean flag1 = world.getType(blockposition.down()).d(world, blockposition.down(), EnumDirection.UP);

            iblockdata = (IBlockData) iblockdata.set(BlockBell.ATTACHMENT, flag1 ? BlockPropertyBellAttach.FLOOR : BlockPropertyBellAttach.CEILING);
            if (iblockdata.canPlace(blockactioncontext.getWorld(), blockactioncontext.getClickPosition())) {
                return iblockdata;
            }
        }

        return null;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyBellAttach blockpropertybellattach = (BlockPropertyBellAttach) iblockdata.get(BlockBell.ATTACHMENT);
        EnumDirection enumdirection1 = n(iblockdata).opposite();

        if (enumdirection1 == enumdirection && !iblockdata.canPlace(generatoraccess, blockposition) && blockpropertybellattach != BlockPropertyBellAttach.DOUBLE_WALL) {
            return Blocks.AIR.getBlockData();
        } else {
            if (enumdirection.n() == ((EnumDirection) iblockdata.get(BlockBell.FACING)).n()) {
                if (blockpropertybellattach == BlockPropertyBellAttach.DOUBLE_WALL && !iblockdata1.d(generatoraccess, blockposition1, enumdirection)) {
                    return (IBlockData) ((IBlockData) iblockdata.set(BlockBell.ATTACHMENT, BlockPropertyBellAttach.SINGLE_WALL)).set(BlockBell.FACING, enumdirection.opposite());
                }

                if (blockpropertybellattach == BlockPropertyBellAttach.SINGLE_WALL && enumdirection1.opposite() == enumdirection && iblockdata1.d(generatoraccess, blockposition1, (EnumDirection) iblockdata.get(BlockBell.FACING))) {
                    return (IBlockData) iblockdata.set(BlockBell.ATTACHMENT, BlockPropertyBellAttach.DOUBLE_WALL);
                }
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = n(iblockdata).opposite();

        return enumdirection == EnumDirection.UP ? Block.a(iworldreader, blockposition.up(), EnumDirection.DOWN) : BlockAttachable.b(iworldreader, blockposition, enumdirection);
    }

    private static EnumDirection n(IBlockData iblockdata) {
        switch ((BlockPropertyBellAttach) iblockdata.get(BlockBell.ATTACHMENT)) {
            case FLOOR:
                return EnumDirection.UP;
            case CEILING:
                return EnumDirection.DOWN;
            default:
                return ((EnumDirection) iblockdata.get(BlockBell.FACING)).opposite();
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBell.FACING, BlockBell.ATTACHMENT, BlockBell.POWERED);
    }

    @Nullable
    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBell(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return a(tileentitytypes, TileEntityTypes.BELL, world.isClientSide ? TileEntityBell::a : TileEntityBell::b);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

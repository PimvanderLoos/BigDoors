package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityLectern;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockLectern extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateBoolean HAS_BOOK = BlockProperties.HAS_BOOK;
    public static final VoxelShape SHAPE_BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final VoxelShape SHAPE_POST = Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
    public static final VoxelShape SHAPE_COMMON = VoxelShapes.or(BlockLectern.SHAPE_BASE, BlockLectern.SHAPE_POST);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final VoxelShape SHAPE_COLLISION = VoxelShapes.or(BlockLectern.SHAPE_COMMON, BlockLectern.SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.or(Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), BlockLectern.SHAPE_COMMON);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.or(Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), BlockLectern.SHAPE_COMMON);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.or(Block.box(10.666667D, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D), Block.box(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D), Block.box(2.0D, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D), BlockLectern.SHAPE_COMMON);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(Block.box(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D), Block.box(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D), Block.box(0.0D, 14.0D, 2.0D, 16.0D, 18.0D, 6.333333D), BlockLectern.SHAPE_COMMON);
    private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

    protected BlockLectern(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockLectern.FACING, EnumDirection.NORTH)).setValue(BlockLectern.POWERED, false)).setValue(BlockLectern.HAS_BOOK, false));
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public VoxelShape getOcclusionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockLectern.SHAPE_COMMON;
    }

    @Override
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return true;
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        ItemStack itemstack = blockactioncontext.getItemInHand();
        EntityHuman entityhuman = blockactioncontext.getPlayer();
        boolean flag = false;

        if (!world.isClientSide && entityhuman != null && entityhuman.canUseGameMasterBlocks()) {
            NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

            if (nbttagcompound != null && nbttagcompound.contains("Book")) {
                flag = true;
            }
        }

        return (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockLectern.FACING, blockactioncontext.getHorizontalDirection().getOpposite())).setValue(BlockLectern.HAS_BOOK, flag);
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockLectern.SHAPE_COLLISION;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection) iblockdata.getValue(BlockLectern.FACING)) {
            case NORTH:
                return BlockLectern.SHAPE_NORTH;
            case SOUTH:
                return BlockLectern.SHAPE_SOUTH;
            case EAST:
                return BlockLectern.SHAPE_EAST;
            case WEST:
                return BlockLectern.SHAPE_WEST;
            default:
                return BlockLectern.SHAPE_COMMON;
        }
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockLectern.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockLectern.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockLectern.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockLectern.FACING, BlockLectern.POWERED, BlockLectern.HAS_BOOK);
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityLectern(blockposition, iblockdata);
    }

    public static boolean tryPlaceBook(@Nullable Entity entity, World world, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        if (!(Boolean) iblockdata.getValue(BlockLectern.HAS_BOOK)) {
            if (!world.isClientSide) {
                placeBook(entity, world, blockposition, iblockdata, itemstack);
            }

            return true;
        } else {
            return false;
        }
    }

    private static void placeBook(@Nullable Entity entity, World world, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityLectern) {
            TileEntityLectern tileentitylectern = (TileEntityLectern) tileentity;

            tileentitylectern.setBook(itemstack.split(1));
            resetBookState(entity, world, blockposition, iblockdata, true);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

    }

    public static void resetBookState(@Nullable Entity entity, World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        IBlockData iblockdata1 = (IBlockData) ((IBlockData) iblockdata.setValue(BlockLectern.POWERED, false)).setValue(BlockLectern.HAS_BOOK, flag);

        world.setBlock(blockposition, iblockdata1, 3);
        world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entity, iblockdata1));
        updateBelow(world, blockposition, iblockdata);
    }

    public static void signalPageChange(World world, BlockPosition blockposition, IBlockData iblockdata) {
        changePowered(world, blockposition, iblockdata, true);
        world.scheduleTick(blockposition, iblockdata.getBlock(), 2);
        world.levelEvent(1043, blockposition, 0);
    }

    private static void changePowered(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockLectern.POWERED, flag), 3);
        updateBelow(world, blockposition, iblockdata);
    }

    private static void updateBelow(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.updateNeighborsAt(blockposition.below(), iblockdata.getBlock());
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        changePowered(worldserver, blockposition, iblockdata, false);
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.getValue(BlockLectern.HAS_BOOK)) {
                this.popBook(iblockdata, world, blockposition);
            }

            if ((Boolean) iblockdata.getValue(BlockLectern.POWERED)) {
                world.updateNeighborsAt(blockposition.below(), this);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    private void popBook(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityLectern) {
            TileEntityLectern tileentitylectern = (TileEntityLectern) tileentity;
            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockLectern.FACING);
            ItemStack itemstack = tileentitylectern.getBook().copy();
            float f = 0.25F * (float) enumdirection.getStepX();
            float f1 = 0.25F * (float) enumdirection.getStepZ();
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + 0.5D + (double) f, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D + (double) f1, itemstack);

            entityitem.setDefaultPickUpDelay();
            world.addFreshEntity(entityitem);
            tileentitylectern.clearContent();
        }

    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockLectern.POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP && (Boolean) iblockdata.getValue(BlockLectern.POWERED) ? 15 : 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if ((Boolean) iblockdata.getValue(BlockLectern.HAS_BOOK)) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityLectern) {
                return ((TileEntityLectern) tileentity).getRedstoneSignal();
            }
        }

        return 0;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.getValue(BlockLectern.HAS_BOOK)) {
            if (!world.isClientSide) {
                this.openScreen(world, blockposition, entityhuman);
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            ItemStack itemstack = entityhuman.getItemInHand(enumhand);

            return !itemstack.isEmpty() && !itemstack.is(TagsItem.LECTERN_BOOKS) ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS;
        }
    }

    @Nullable
    @Override
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return !(Boolean) iblockdata.getValue(BlockLectern.HAS_BOOK) ? null : super.getMenuProvider(iblockdata, world, blockposition);
    }

    private void openScreen(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityLectern) {
            entityhuman.openMenu((TileEntityLectern) tileentity);
            entityhuman.awardStat(StatisticList.INTERACT_WITH_LECTERN);
        }

    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

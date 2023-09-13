package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
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
    public static final VoxelShape SHAPE_BASE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final VoxelShape SHAPE_POST = Block.a(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
    public static final VoxelShape SHAPE_COMMON = VoxelShapes.a(BlockLectern.SHAPE_BASE, BlockLectern.SHAPE_POST);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.a(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final VoxelShape SHAPE_COLLISION = VoxelShapes.a(BlockLectern.SHAPE_COMMON, BlockLectern.SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.a(Block.a(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.a(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.a(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), BlockLectern.SHAPE_COMMON);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.a(Block.a(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.a(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.a(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), BlockLectern.SHAPE_COMMON);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.a(Block.a(10.666667D, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D), Block.a(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D), Block.a(2.0D, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D), BlockLectern.SHAPE_COMMON);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.a(Block.a(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D), Block.a(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D), Block.a(0.0D, 14.0D, 2.0D, 16.0D, 18.0D, 6.333333D), BlockLectern.SHAPE_COMMON);
    private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

    protected BlockLectern(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockLectern.FACING, EnumDirection.NORTH)).set(BlockLectern.POWERED, false)).set(BlockLectern.HAS_BOOK, false));
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockLectern.SHAPE_COMMON;
    }

    @Override
    public boolean g_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        ItemStack itemstack = blockactioncontext.getItemStack();
        NBTTagCompound nbttagcompound = itemstack.getTag();
        EntityHuman entityhuman = blockactioncontext.getEntity();
        boolean flag = false;

        if (!world.isClientSide && entityhuman != null && nbttagcompound != null && entityhuman.isCreativeAndOp() && nbttagcompound.hasKey("BlockEntityTag")) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("BlockEntityTag");

            if (nbttagcompound1.hasKey("Book")) {
                flag = true;
            }
        }

        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockLectern.FACING, blockactioncontext.g().opposite())).set(BlockLectern.HAS_BOOK, flag);
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockLectern.SHAPE_COLLISION;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection) iblockdata.get(BlockLectern.FACING)) {
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
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockLectern.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockLectern.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockLectern.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLectern.FACING, BlockLectern.POWERED, BlockLectern.HAS_BOOK);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityLectern(blockposition, iblockdata);
    }

    public static boolean a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        if (!(Boolean) iblockdata.get(BlockLectern.HAS_BOOK)) {
            if (!world.isClientSide) {
                b(entityhuman, world, blockposition, iblockdata, itemstack);
            }

            return true;
        } else {
            return false;
        }
    }

    private static void b(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityLectern) {
            TileEntityLectern tileentitylectern = (TileEntityLectern) tileentity;

            tileentitylectern.setBook(itemstack.cloneAndSubtract(1));
            setHasBook(world, blockposition, iblockdata, true);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.a((Entity) entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
        }

    }

    public static void setHasBook(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) iblockdata.set(BlockLectern.POWERED, false)).set(BlockLectern.HAS_BOOK, flag), 3);
        b(world, blockposition, iblockdata);
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        b(world, blockposition, iblockdata, true);
        world.getBlockTickList().a(blockposition, iblockdata.getBlock(), 2);
        world.triggerEffect(1043, blockposition, 0);
    }

    private static void b(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockLectern.POWERED, flag), 3);
        b(world, blockposition, iblockdata);
    }

    private static void b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.applyPhysics(blockposition.down(), iblockdata.getBlock());
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        b(worldserver, blockposition, iblockdata, false);
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.get(BlockLectern.HAS_BOOK)) {
                this.d(iblockdata, world, blockposition);
            }

            if ((Boolean) iblockdata.get(BlockLectern.POWERED)) {
                world.applyPhysics(blockposition.down(), this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    private void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityLectern) {
            TileEntityLectern tileentitylectern = (TileEntityLectern) tileentity;
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockLectern.FACING);
            ItemStack itemstack = tileentitylectern.getBook().cloneItemStack();
            float f = 0.25F * (float) enumdirection.getAdjacentX();
            float f1 = 0.25F * (float) enumdirection.getAdjacentZ();
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + 0.5D + (double) f, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D + (double) f1, itemstack);

            entityitem.defaultPickupDelay();
            world.addEntity(entityitem);
            tileentitylectern.clear();
        }

    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockLectern.POWERED) ? 15 : 0;
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP && (Boolean) iblockdata.get(BlockLectern.POWERED) ? 15 : 0;
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if ((Boolean) iblockdata.get(BlockLectern.HAS_BOOK)) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityLectern) {
                return ((TileEntityLectern) tileentity).i();
            }
        }

        return 0;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.get(BlockLectern.HAS_BOOK)) {
            if (!world.isClientSide) {
                this.a(world, blockposition, entityhuman);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            ItemStack itemstack = entityhuman.b(enumhand);

            return !itemstack.isEmpty() && !itemstack.a((Tag) TagsItem.LECTERN_BOOKS) ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS;
        }
    }

    @Nullable
    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return !(Boolean) iblockdata.get(BlockLectern.HAS_BOOK) ? null : super.getInventory(iblockdata, world, blockposition);
    }

    private void a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityLectern) {
            entityhuman.openContainer((TileEntityLectern) tileentity);
            entityhuman.a(StatisticList.INTERACT_WITH_LECTERN);
        }

    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

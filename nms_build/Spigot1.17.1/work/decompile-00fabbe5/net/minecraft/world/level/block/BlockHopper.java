package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InventoryUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.IHopper;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockHopper extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockProperties.FACING_HOPPER;
    public static final BlockStateBoolean ENABLED = BlockProperties.ENABLED;
    private static final VoxelShape TOP = Block.a(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape FUNNEL = Block.a(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape CONVEX_BASE = VoxelShapes.a(BlockHopper.FUNNEL, BlockHopper.TOP);
    private static final VoxelShape BASE = VoxelShapes.a(BlockHopper.CONVEX_BASE, IHopper.INSIDE, OperatorBoolean.ONLY_FIRST);
    private static final VoxelShape DOWN_SHAPE = VoxelShapes.a(BlockHopper.BASE, Block.a(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
    private static final VoxelShape EAST_SHAPE = VoxelShapes.a(BlockHopper.BASE, Block.a(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.a(BlockHopper.BASE, Block.a(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.a(BlockHopper.BASE, Block.a(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
    private static final VoxelShape WEST_SHAPE = VoxelShapes.a(BlockHopper.BASE, Block.a(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
    private static final VoxelShape DOWN_INTERACTION_SHAPE = IHopper.INSIDE;
    private static final VoxelShape EAST_INTERACTION_SHAPE = VoxelShapes.a(IHopper.INSIDE, Block.a(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
    private static final VoxelShape NORTH_INTERACTION_SHAPE = VoxelShapes.a(IHopper.INSIDE, Block.a(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
    private static final VoxelShape SOUTH_INTERACTION_SHAPE = VoxelShapes.a(IHopper.INSIDE, Block.a(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
    private static final VoxelShape WEST_INTERACTION_SHAPE = VoxelShapes.a(IHopper.INSIDE, Block.a(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

    public BlockHopper(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockHopper.FACING, EnumDirection.DOWN)).set(BlockHopper.ENABLED, true));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection) iblockdata.get(BlockHopper.FACING)) {
            case DOWN:
                return BlockHopper.DOWN_SHAPE;
            case NORTH:
                return BlockHopper.NORTH_SHAPE;
            case SOUTH:
                return BlockHopper.SOUTH_SHAPE;
            case WEST:
                return BlockHopper.WEST_SHAPE;
            case EAST:
                return BlockHopper.EAST_SHAPE;
            default:
                return BlockHopper.BASE;
        }
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockHopper.FACING)) {
            case DOWN:
                return BlockHopper.DOWN_INTERACTION_SHAPE;
            case NORTH:
                return BlockHopper.NORTH_INTERACTION_SHAPE;
            case SOUTH:
                return BlockHopper.SOUTH_INTERACTION_SHAPE;
            case WEST:
                return BlockHopper.WEST_INTERACTION_SHAPE;
            case EAST:
                return BlockHopper.EAST_INTERACTION_SHAPE;
            default:
                return IHopper.INSIDE;
        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace().opposite();

        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockHopper.FACING, enumdirection.n() == EnumDirection.EnumAxis.Y ? EnumDirection.DOWN : enumdirection)).set(BlockHopper.ENABLED, true);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityHopper(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? null : a(tileentitytypes, TileEntityTypes.HOPPER, TileEntityHopper::a);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                ((TileEntityHopper) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock())) {
            this.a(world, blockposition, iblockdata);
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                entityhuman.openContainer((TileEntityHopper) tileentity);
                entityhuman.a(StatisticList.INSPECT_HOPPER);
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        this.a(world, blockposition, iblockdata);
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = !world.isBlockIndirectlyPowered(blockposition);

        if (flag != (Boolean) iblockdata.get(BlockHopper.ENABLED)) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockHopper.ENABLED, flag), 4);
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                InventoryUtils.dropInventory(world, blockposition, (TileEntityHopper) tileentity);
                world.updateAdjacentComparators(blockposition, this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockHopper.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockHopper.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockHopper.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockHopper.FACING, BlockHopper.ENABLED);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityHopper) {
            TileEntityHopper.a(world, blockposition, iblockdata, entity, (TileEntityHopper) tileentity);
        }

    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

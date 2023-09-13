package net.minecraft.server;

import java.util.Random;

public class BlockEnderChest extends BlockTileEntity implements IFluidSource, IFluidContainer {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean b = BlockProperties.y;
    protected static final VoxelShape c = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    protected BlockEnderChest(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockEnderChest.FACING, EnumDirection.NORTH)).set(BlockEnderChest.b, false));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockEnderChest.c;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Blocks.OBSIDIAN;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 8;
    }

    protected boolean X_() {
        return true;
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockEnderChest.FACING, blockactioncontext.f().opposite())).set(BlockEnderChest.b, fluid.c() == FluidTypes.WATER);
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        InventoryEnderChest inventoryenderchest = entityhuman.getEnderChest();
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (inventoryenderchest != null && tileentity instanceof TileEntityEnderChest) {
            if (world.getType(blockposition.up()).isOccluding()) {
                return true;
            } else if (world.isClientSide) {
                return true;
            } else {
                inventoryenderchest.a((TileEntityEnderChest) tileentity);
                entityhuman.openContainer(inventoryenderchest);
                entityhuman.a(StatisticList.OPEN_ENDERCHEST);
                return true;
            }
        } else {
            return true;
        }
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityEnderChest();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockEnderChest.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEnderChest.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockEnderChest.FACING)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockEnderChest.FACING, BlockEnderChest.b);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockEnderChest.b)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockEnderChest.b, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockEnderChest.b) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockEnderChest.b) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockEnderChest.b) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockEnderChest.b, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockEnderChest.b)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}

package net.minecraft.server;

public abstract class BlockSign extends BlockTileEntity implements IFluidSource, IFluidContainer {

    public static final BlockStateBoolean a = BlockProperties.y;
    protected static final VoxelShape b = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    protected BlockSign(Block.Info block_info) {
        super(block_info);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (((Boolean) iblockdata.get(BlockSign.a)).booleanValue()) {
            generatoraccess.I().a(blockposition, FluidTypes.c, FluidTypes.c.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSign.b;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean a() {
        return true;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntitySign();
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            return tileentity instanceof TileEntitySign && ((TileEntitySign) tileentity).b(entityhuman);
        }
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public FluidType a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (((Boolean) iblockdata.get(BlockSign.a)).booleanValue()) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSign.a, Boolean.valueOf(false)), 3);
            return FluidTypes.c;
        } else {
            return FluidTypes.a;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return ((Boolean) iblockdata.get(BlockSign.a)).booleanValue() ? FluidTypes.c.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !((Boolean) iblockdata.get(BlockSign.a)).booleanValue() && fluidtype == FluidTypes.c;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!((Boolean) iblockdata.get(BlockSign.a)).booleanValue() && fluid.c() == FluidTypes.c) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSign.a, Boolean.valueOf(true)), 3);
                generatoraccess.I().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }
}

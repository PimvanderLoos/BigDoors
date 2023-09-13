package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockStepAbstract extends Block implements IFluidSource, IFluidContainer {

    public static final BlockStateEnum<BlockPropertySlabType> a = BlockProperties.au;
    public static final BlockStateBoolean b = BlockProperties.y;
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape o = Block.a(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockStepAbstract(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.getBlockData().set(BlockStepAbstract.a, BlockPropertySlabType.BOTTOM)).set(BlockStepAbstract.b, false));
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.K();
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStepAbstract.a, BlockStepAbstract.b);
    }

    protected boolean X_() {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPropertySlabType blockpropertyslabtype = (BlockPropertySlabType) iblockdata.get(BlockStepAbstract.a);

        switch (blockpropertyslabtype) {
        case DOUBLE:
            return VoxelShapes.b();
        case TOP:
            return BlockStepAbstract.o;
        default:
            return BlockStepAbstract.c;
        }
    }

    public boolean r(IBlockData iblockdata) {
        return iblockdata.get(BlockStepAbstract.a) == BlockPropertySlabType.DOUBLE || iblockdata.get(BlockStepAbstract.a) == BlockPropertySlabType.TOP;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPropertySlabType blockpropertyslabtype = (BlockPropertySlabType) iblockdata.get(BlockStepAbstract.a);

        return blockpropertyslabtype == BlockPropertySlabType.DOUBLE ? EnumBlockFaceShape.SOLID : (enumdirection == EnumDirection.UP && blockpropertyslabtype == BlockPropertySlabType.TOP ? EnumBlockFaceShape.SOLID : (enumdirection == EnumDirection.DOWN && blockpropertyslabtype == BlockPropertySlabType.BOTTOM ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED));
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition());

        if (iblockdata.getBlock() == this) {
            return (IBlockData) ((IBlockData) iblockdata.set(BlockStepAbstract.a, BlockPropertySlabType.DOUBLE)).set(BlockStepAbstract.b, false);
        } else {
            Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) this.getBlockData().set(BlockStepAbstract.a, BlockPropertySlabType.BOTTOM)).set(BlockStepAbstract.b, fluid.c() == FluidTypes.WATER);
            EnumDirection enumdirection = blockactioncontext.getClickedFace();

            return enumdirection != EnumDirection.DOWN && (enumdirection == EnumDirection.UP || (double) blockactioncontext.n() <= 0.5D) ? iblockdata1 : (IBlockData) iblockdata1.set(BlockStepAbstract.a, BlockPropertySlabType.TOP);
        }
    }

    public int a(IBlockData iblockdata, Random random) {
        return iblockdata.get(BlockStepAbstract.a) == BlockPropertySlabType.DOUBLE ? 2 : 1;
    }

    public boolean a(IBlockData iblockdata) {
        return iblockdata.get(BlockStepAbstract.a) == BlockPropertySlabType.DOUBLE;
    }

    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        ItemStack itemstack = blockactioncontext.getItemStack();
        BlockPropertySlabType blockpropertyslabtype = (BlockPropertySlabType) iblockdata.get(BlockStepAbstract.a);

        if (blockpropertyslabtype != BlockPropertySlabType.DOUBLE && itemstack.getItem() == this.getItem()) {
            if (blockactioncontext.c()) {
                boolean flag = (double) blockactioncontext.n() > 0.5D;
                EnumDirection enumdirection = blockactioncontext.getClickedFace();

                return blockpropertyslabtype == BlockPropertySlabType.BOTTOM ? enumdirection == EnumDirection.UP || flag && enumdirection.k().c() : enumdirection == EnumDirection.DOWN || !flag && enumdirection.k().c();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockStepAbstract.b)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockStepAbstract.b, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockStepAbstract.b) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return iblockdata.get(BlockStepAbstract.a) != BlockPropertySlabType.DOUBLE && !(Boolean) iblockdata.get(BlockStepAbstract.b) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (iblockdata.get(BlockStepAbstract.a) != BlockPropertySlabType.DOUBLE && !(Boolean) iblockdata.get(BlockStepAbstract.b) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockStepAbstract.b, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockStepAbstract.b)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
        case LAND:
            return iblockdata.get(BlockStepAbstract.a) == BlockPropertySlabType.BOTTOM;
        case WATER:
            return iblockaccess.getFluid(blockposition).a(TagsFluid.WATER);
        case AIR:
            return false;
        default:
            return false;
        }
    }
}

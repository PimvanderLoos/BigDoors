package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockLadder extends Block implements IFluidSource, IFluidContainer {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean b = BlockProperties.y;
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    protected static final VoxelShape o = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape p = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape q = Block.a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);

    protected BlockLadder(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockLadder.FACING, EnumDirection.NORTH)).set(BlockLadder.b, false));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockLadder.FACING)) {
        case NORTH:
            return BlockLadder.q;
        case SOUTH:
            return BlockLadder.p;
        case WEST:
            return BlockLadder.o;
        case EAST:
        default:
            return BlockLadder.c;
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        boolean flag = b(iblockdata.getBlock());

        return !flag && iblockdata.c(iblockaccess, blockposition, enumdirection) == EnumBlockFaceShape.SOLID && !iblockdata.isPowerSource();
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockLadder.FACING);

        return this.a((IBlockAccess) iworldreader, blockposition.shift(enumdirection.opposite()), enumdirection);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection.opposite() == iblockdata.get(BlockLadder.FACING) && !iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            if ((Boolean) iblockdata.get(BlockLadder.b)) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata;

        if (!blockactioncontext.c()) {
            iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(blockactioncontext.getClickedFace().opposite()));
            if (iblockdata.getBlock() == this && iblockdata.get(BlockLadder.FACING) == blockactioncontext.getClickedFace()) {
                return null;
            }
        }

        iblockdata = this.getBlockData();
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        EnumDirection[] aenumdirection = blockactioncontext.e();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.k().c()) {
                iblockdata = (IBlockData) iblockdata.set(BlockLadder.FACING, enumdirection.opposite());
                if (iblockdata.canPlace(world, blockposition)) {
                    return (IBlockData) iblockdata.set(BlockLadder.b, fluid.c() == FluidTypes.WATER);
                }
            }
        }

        return null;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockLadder.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockLadder.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockLadder.FACING)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLadder.FACING, BlockLadder.b);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockLadder.b)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockLadder.b, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockLadder.b) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockLadder.b) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockLadder.b) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockLadder.b, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }
}

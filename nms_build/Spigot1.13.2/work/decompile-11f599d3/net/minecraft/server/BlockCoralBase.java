package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockCoralBase extends Block implements IFluidSource, IFluidContainer {

    public static final BlockStateBoolean b = BlockProperties.y;
    private static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    protected BlockCoralBase(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCoralBase.b, true));
    }

    protected void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!b_(iblockdata, generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 60 + generatoraccess.m().nextInt(40));
        }

    }

    protected static boolean b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if ((Boolean) iblockdata.get(BlockCoralBase.b)) {
            return true;
        } else {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (iblockaccess.getFluid(blockposition.shift(enumdirection)).a(TagsFluid.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    protected boolean X_() {
        return true;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        return (IBlockData) this.getBlockData().set(BlockCoralBase.b, fluid.a(TagsFluid.WATER) && fluid.g() == 8);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCoralBase.a;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockCoralBase.b)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection == EnumDirection.DOWN && !this.canPlace(iblockdata, generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getType(blockposition.down()).q();
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCoralBase.b);
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockCoralBase.b) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockCoralBase.b)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCoralBase.b, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockCoralBase.b) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockCoralBase.b) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCoralBase.b, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }
}

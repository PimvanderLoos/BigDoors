package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockCoralFanAbstract extends Block implements IFluidSource, IFluidContainer {

    public static final BlockStateBoolean a = BlockProperties.x;
    private static final VoxelShape b = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    protected BlockCoralFanAbstract(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCoralFanAbstract.a, Boolean.valueOf(true)));
    }

    protected void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!b_(iblockdata, generatoraccess, blockposition)) {
            generatoraccess.I().a(blockposition, this, 60 + generatoraccess.m().nextInt(40));
        }

    }

    protected static boolean b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (((Boolean) iblockdata.get(BlockCoralFanAbstract.a)).booleanValue()) {
            return true;
        } else {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (iblockaccess.b(blockposition.shift(enumdirection)).a(TagsFluid.a)) {
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
        Fluid fluid = blockactioncontext.getWorld().b(blockactioncontext.getClickPosition());

        return (IBlockData) this.getBlockData().set(BlockCoralFanAbstract.a, Boolean.valueOf(fluid.a(TagsFluid.a) && fluid.g() == 8));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCoralFanAbstract.b;
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
        if (((Boolean) iblockdata.get(BlockCoralFanAbstract.a)).booleanValue()) {
            generatoraccess.H().a(blockposition, FluidTypes.c, FluidTypes.c.a((IWorldReader) generatoraccess));
        }

        return enumdirection == EnumDirection.DOWN && !this.canPlace(iblockdata, generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getType(blockposition.down()).q();
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(new IBlockState[] { BlockCoralFanAbstract.a});
    }

    public Fluid h(IBlockData iblockdata) {
        return ((Boolean) iblockdata.get(BlockCoralFanAbstract.a)).booleanValue() ? FluidTypes.c.a(false) : super.h(iblockdata);
    }

    public FluidType a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (((Boolean) iblockdata.get(BlockCoralFanAbstract.a)).booleanValue()) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCoralFanAbstract.a, Boolean.valueOf(false)), 3);
            return FluidTypes.c;
        } else {
            return FluidTypes.a;
        }
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !((Boolean) iblockdata.get(BlockCoralFanAbstract.a)).booleanValue() && fluidtype == FluidTypes.c;
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!((Boolean) iblockdata.get(BlockCoralFanAbstract.a)).booleanValue() && fluid.c() == FluidTypes.c) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCoralFanAbstract.a, Boolean.valueOf(true)), 3);
                generatoraccess.H().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }
}

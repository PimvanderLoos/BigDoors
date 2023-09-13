package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockTallSeaGrass extends BlockTallPlantShearable implements IFluidContainer {

    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> c = BlockTallPlantShearable.b;
    protected static final VoxelShape o = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public BlockTallSeaGrass(Block block, Block.Info block_info) {
        super(block, block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockTallSeaGrass.o;
    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Block.a(iblockdata.getCollisionShape(iblockaccess, blockposition), EnumDirection.UP) && iblockdata.getBlock() != Blocks.MAGMA_BLOCK;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.SEAGRASS);
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);

        if (iblockdata != null) {
            Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition().up());

            if (fluid.a(TagsFluid.WATER) && fluid.g() == 8) {
                return iblockdata;
            }
        }

        return null;
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.get(BlockTallSeaGrass.c) == BlockPropertyDoubleBlockHalf.UPPER) {
            IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

            return iblockdata1.getBlock() == this && iblockdata1.get(BlockTallSeaGrass.c) == BlockPropertyDoubleBlockHalf.LOWER;
        } else {
            Fluid fluid = iworldreader.getFluid(blockposition);

            return super.canPlace(iblockdata, iworldreader, blockposition) && fluid.a(TagsFluid.WATER) && fluid.g() == 8;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Blocks.WATER.getBlockData().b(iblockaccess, blockposition);
    }
}

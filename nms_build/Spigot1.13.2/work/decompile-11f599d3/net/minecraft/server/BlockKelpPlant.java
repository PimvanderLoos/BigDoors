package net.minecraft.server;

public class BlockKelpPlant extends Block implements IFluidContainer {

    final BlockKelp a;

    protected BlockKelpPlant(BlockKelp blockkelp, Block.Info block_info) {
        super(block_info);
        this.a = blockkelp;
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

    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            if (enumdirection == EnumDirection.UP) {
                Block block = iblockdata1.getBlock();

                if (block != this && block != this.a) {
                    return this.a.a(generatoraccess);
                }
            }

            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        Block block = iblockdata1.getBlock();

        return block != Blocks.MAGMA_BLOCK && (block == this || Block.a(iblockdata1.getCollisionShape(iworldreader, blockposition1), EnumDirection.UP));
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Blocks.KELP;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.KELP);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }
}

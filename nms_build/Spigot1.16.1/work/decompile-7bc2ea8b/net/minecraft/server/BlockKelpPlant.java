package net.minecraft.server;

public class BlockKelpPlant extends BlockGrowingStem implements IFluidContainer {

    protected BlockKelpPlant(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.UP, VoxelShapes.b(), true);
    }

    @Override
    protected BlockGrowingTop c() {
        return (BlockGrowingTop) Blocks.KELP;
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    @Override
    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    @Override
    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }
}

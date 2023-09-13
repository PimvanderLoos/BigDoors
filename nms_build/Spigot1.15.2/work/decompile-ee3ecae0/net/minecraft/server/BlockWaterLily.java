package net.minecraft.server;

public class BlockWaterLily extends BlockPlant {

    protected static final VoxelShape a = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    protected BlockWaterLily(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        super.a(iblockdata, world, blockposition, entity);
        if (world instanceof WorldServer && entity instanceof EntityBoat) {
            world.a(new BlockPosition(blockposition), true, entity);
        }

    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockWaterLily.a;
    }

    @Override
    protected boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Fluid fluid = iblockaccess.getFluid(blockposition);

        return fluid.getType() == FluidTypes.WATER || iblockdata.getMaterial() == Material.ICE;
    }
}

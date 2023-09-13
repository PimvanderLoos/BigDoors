package net.minecraft.server;

public class BlockPotatoes extends BlockCrops {

    private static final VoxelShape[] a = new VoxelShape[] { Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

    public BlockPotatoes(Block.Info block_info) {
        super(block_info);
    }

    protected IMaterial f() {
        return Items.POTATO;
    }

    protected IMaterial g() {
        return Items.POTATO;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        super.dropNaturally(iblockdata, world, blockposition, f, i);
        if (!world.isClientSide) {
            if (this.w(iblockdata) && world.random.nextInt(50) == 0) {
                a(world, blockposition, new ItemStack(Items.POISONOUS_POTATO));
            }

        }
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockPotatoes.a[(Integer) iblockdata.get(this.d())];
    }
}

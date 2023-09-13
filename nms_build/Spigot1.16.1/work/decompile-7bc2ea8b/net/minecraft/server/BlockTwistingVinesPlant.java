package net.minecraft.server;

public class BlockTwistingVinesPlant extends BlockGrowingStem {

    public static final VoxelShape d = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public BlockTwistingVinesPlant(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.UP, BlockTwistingVinesPlant.d, false);
    }

    @Override
    protected BlockGrowingTop c() {
        return (BlockGrowingTop) Blocks.TWISTING_VINES;
    }
}

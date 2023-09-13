package net.minecraft.server;

import java.util.Random;

public class BlockWeepingVines extends BlockGrowingTop {

    protected static final VoxelShape e = Block.a(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public BlockWeepingVines(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.DOWN, BlockWeepingVines.e, false, 0.1D);
    }

    @Override
    protected int a(Random random) {
        return BlockNetherVinesUtil.a(random);
    }

    @Override
    protected Block d() {
        return Blocks.WEEPING_VINES_PLANT;
    }

    @Override
    protected boolean h(IBlockData iblockdata) {
        return BlockNetherVinesUtil.a(iblockdata);
    }
}

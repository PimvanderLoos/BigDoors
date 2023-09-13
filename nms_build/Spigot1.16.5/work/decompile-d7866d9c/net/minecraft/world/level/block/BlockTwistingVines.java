package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockTwistingVines extends BlockGrowingTop {

    public static final VoxelShape e = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);

    public BlockTwistingVines(BlockBase.Info blockbase_info) {
        super(blockbase_info, EnumDirection.UP, BlockTwistingVines.e, false, 0.1D);
    }

    @Override
    protected int a(Random random) {
        return BlockNetherVinesUtil.a(random);
    }

    @Override
    protected Block d() {
        return Blocks.TWISTING_VINES_PLANT;
    }

    @Override
    protected boolean h(IBlockData iblockdata) {
        return BlockNetherVinesUtil.a(iblockdata);
    }
}

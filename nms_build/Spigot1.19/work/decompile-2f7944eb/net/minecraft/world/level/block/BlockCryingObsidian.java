package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockCryingObsidian extends Block {

    public BlockCryingObsidian(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if (randomsource.nextInt(5) == 0) {
            EnumDirection enumdirection = EnumDirection.getRandom(randomsource);

            if (enumdirection != EnumDirection.UP) {
                BlockPosition blockposition1 = blockposition.relative(enumdirection);
                IBlockData iblockdata1 = world.getBlockState(blockposition1);

                if (!iblockdata.canOcclude() || !iblockdata1.isFaceSturdy(world, blockposition1, enumdirection.getOpposite())) {
                    double d0 = enumdirection.getStepX() == 0 ? randomsource.nextDouble() : 0.5D + (double) enumdirection.getStepX() * 0.6D;
                    double d1 = enumdirection.getStepY() == 0 ? randomsource.nextDouble() : 0.5D + (double) enumdirection.getStepY() * 0.6D;
                    double d2 = enumdirection.getStepZ() == 0 ? randomsource.nextDouble() : 0.5D + (double) enumdirection.getStepZ() * 0.6D;

                    world.addParticle(Particles.DRIPPING_OBSIDIAN_TEAR, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}

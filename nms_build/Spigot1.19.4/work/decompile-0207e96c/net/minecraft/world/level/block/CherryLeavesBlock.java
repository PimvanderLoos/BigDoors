package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class CherryLeavesBlock extends BlockLeaves {

    public CherryLeavesBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        super.animateTick(iblockdata, world, blockposition, randomsource);
        if (randomsource.nextInt(15) == 0) {
            BlockPosition blockposition1 = blockposition.below();
            IBlockData iblockdata1 = world.getBlockState(blockposition1);

            if (!iblockdata1.canOcclude() || !iblockdata1.isFaceSturdy(world, blockposition1, EnumDirection.UP)) {
                ParticleUtils.spawnParticleBelow(world, blockposition, randomsource, Particles.DRIPPING_CHERRY_LEAVES);
            }
        }
    }
}

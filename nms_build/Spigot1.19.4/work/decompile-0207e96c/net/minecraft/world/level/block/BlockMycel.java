package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockMycel extends BlockDirtSnowSpreadable {

    public BlockMycel(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        super.animateTick(iblockdata, world, blockposition, randomsource);
        if (randomsource.nextInt(10) == 0) {
            world.addParticle(Particles.MYCELIUM, (double) blockposition.getX() + randomsource.nextDouble(), (double) blockposition.getY() + 1.1D, (double) blockposition.getZ() + randomsource.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }
}

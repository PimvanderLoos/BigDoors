package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockMycel extends BlockDirtSnowSpreadable {

    public BlockMycel(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.a(iblockdata, world, blockposition, random);
        if (random.nextInt(10) == 0) {
            world.addParticle(Particles.MYCELIUM, (double) blockposition.getX() + random.nextDouble(), (double) blockposition.getY() + 1.1D, (double) blockposition.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }
}

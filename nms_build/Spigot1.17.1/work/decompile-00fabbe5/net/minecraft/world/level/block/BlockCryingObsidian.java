package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockCryingObsidian extends Block {

    public BlockCryingObsidian(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (random.nextInt(5) == 0) {
            EnumDirection enumdirection = EnumDirection.a(random);

            if (enumdirection != EnumDirection.UP) {
                BlockPosition blockposition1 = blockposition.shift(enumdirection);
                IBlockData iblockdata1 = world.getType(blockposition1);

                if (!iblockdata.l() || !iblockdata1.d(world, blockposition1, enumdirection.opposite())) {
                    double d0 = enumdirection.getAdjacentX() == 0 ? random.nextDouble() : 0.5D + (double) enumdirection.getAdjacentX() * 0.6D;
                    double d1 = enumdirection.getAdjacentY() == 0 ? random.nextDouble() : 0.5D + (double) enumdirection.getAdjacentY() * 0.6D;
                    double d2 = enumdirection.getAdjacentZ() == 0 ? random.nextDouble() : 0.5D + (double) enumdirection.getAdjacentZ() * 0.6D;

                    world.addParticle(Particles.DRIPPING_OBSIDIAN_TEAR, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}

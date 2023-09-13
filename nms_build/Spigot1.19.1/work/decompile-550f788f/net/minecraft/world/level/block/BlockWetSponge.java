package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockWetSponge extends Block {

    protected BlockWetSponge(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (world.dimensionType().ultraWarm()) {
            world.setBlock(blockposition, Blocks.SPONGE.defaultBlockState(), 3);
            world.levelEvent(2009, blockposition, 0);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }

    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        EnumDirection enumdirection = EnumDirection.getRandom(randomsource);

        if (enumdirection != EnumDirection.UP) {
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            IBlockData iblockdata1 = world.getBlockState(blockposition1);

            if (!iblockdata.canOcclude() || !iblockdata1.isFaceSturdy(world, blockposition1, enumdirection.getOpposite())) {
                double d0 = (double) blockposition.getX();
                double d1 = (double) blockposition.getY();
                double d2 = (double) blockposition.getZ();

                if (enumdirection == EnumDirection.DOWN) {
                    d1 -= 0.05D;
                    d0 += randomsource.nextDouble();
                    d2 += randomsource.nextDouble();
                } else {
                    d1 += randomsource.nextDouble() * 0.8D;
                    if (enumdirection.getAxis() == EnumDirection.EnumAxis.X) {
                        d2 += randomsource.nextDouble();
                        if (enumdirection == EnumDirection.EAST) {
                            ++d0;
                        } else {
                            d0 += 0.05D;
                        }
                    } else {
                        d0 += randomsource.nextDouble();
                        if (enumdirection == EnumDirection.SOUTH) {
                            ++d2;
                        } else {
                            d2 += 0.05D;
                        }
                    }
                }

                world.addParticle(Particles.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}

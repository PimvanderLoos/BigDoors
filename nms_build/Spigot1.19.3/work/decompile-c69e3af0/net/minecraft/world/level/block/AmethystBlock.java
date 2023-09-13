package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class AmethystBlock extends Block {

    public AmethystBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        if (!world.isClientSide) {
            BlockPosition blockposition = movingobjectpositionblock.getBlockPos();

            world.playSound((EntityHuman) null, blockposition, SoundEffects.AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1.0F, 0.5F + world.random.nextFloat() * 1.2F);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0F, 0.5F + world.random.nextFloat() * 1.2F);
        }

    }
}

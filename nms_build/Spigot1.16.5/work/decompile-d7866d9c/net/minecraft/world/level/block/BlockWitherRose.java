package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockWitherRose extends BlockFlowers {

    public BlockWitherRose(MobEffectList mobeffectlist, BlockBase.Info blockbase_info) {
        super(mobeffectlist, 8, blockbase_info);
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return super.c(iblockdata, iblockaccess, blockposition) || iblockdata.a(Blocks.NETHERRACK) || iblockdata.a(Blocks.SOUL_SAND) || iblockdata.a(Blocks.SOUL_SOIL);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!entityliving.isInvulnerable(DamageSource.WITHER)) {
                    entityliving.addEffect(new MobEffect(MobEffects.WITHER, 40));
                }
            }

        }
    }
}

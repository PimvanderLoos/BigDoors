package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
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
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockWitherRose extends BlockFlowers {

    public BlockWitherRose(MobEffectList mobeffectlist, BlockBase.Info blockbase_info) {
        super(mobeffectlist, 8, blockbase_info);
    }

    @Override
    protected boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return super.d(iblockdata, iblockaccess, blockposition) || iblockdata.a(Blocks.NETHERRACK) || iblockdata.a(Blocks.SOUL_SAND) || iblockdata.a(Blocks.SOUL_SOIL);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        VoxelShape voxelshape = this.a(iblockdata, (IBlockAccess) world, blockposition, VoxelShapeCollision.a());
        Vec3D vec3d = voxelshape.getBoundingBox().f();
        double d0 = (double) blockposition.getX() + vec3d.x;
        double d1 = (double) blockposition.getZ() + vec3d.z;

        for (int i = 0; i < 3; ++i) {
            if (random.nextBoolean()) {
                world.addParticle(Particles.SMOKE, d0 + random.nextDouble() / 5.0D, (double) blockposition.getY() + (0.5D - random.nextDouble()), d1 + random.nextDouble() / 5.0D, 0.0D, 0.0D, 0.0D);
            }
        }

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

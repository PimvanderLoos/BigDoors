package net.minecraft.world.level.block;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockHoney extends BlockHalfTransparent {

    private static final double SLIDE_STARTS_WHEN_VERTICAL_SPEED_IS_AT_LEAST = 0.13D;
    private static final double MIN_FALL_SPEED_TO_BE_CONSIDERED_SLIDING = 0.08D;
    private static final double THROTTLE_SLIDE_SPEED_TO = 0.05D;
    private static final int SLIDE_ADVANCEMENT_CHECK_INTERVAL = 20;
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public BlockHoney(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    private static boolean doesEntityDoHoneyBlockSlideEffects(Entity entity) {
        return entity instanceof EntityLiving || entity instanceof EntityMinecartAbstract || entity instanceof EntityTNTPrimed || entity instanceof EntityBoat;
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockHoney.SHAPE;
    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        entity.playSound(SoundEffects.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (!world.isClientSide) {
            world.broadcastEntityEvent(entity, (byte) 54);
        }

        if (entity.causeFallDamage(f, 0.2F, DamageSource.FALL)) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }

    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (this.isSlidingDown(blockposition, entity)) {
            this.maybeDoSlideAchievement(entity, blockposition);
            this.doSlideMovement(entity);
            this.maybeDoSlideEffects(world, entity);
        }

        super.entityInside(iblockdata, world, blockposition, entity);
    }

    private boolean isSlidingDown(BlockPosition blockposition, Entity entity) {
        if (entity.isOnGround()) {
            return false;
        } else if (entity.getY() > (double) blockposition.getY() + 0.9375D - 1.0E-7D) {
            return false;
        } else if (entity.getDeltaMovement().y >= -0.08D) {
            return false;
        } else {
            double d0 = Math.abs((double) blockposition.getX() + 0.5D - entity.getX());
            double d1 = Math.abs((double) blockposition.getZ() + 0.5D - entity.getZ());
            double d2 = 0.4375D + (double) (entity.getBbWidth() / 2.0F);

            return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
        }
    }

    private void maybeDoSlideAchievement(Entity entity, BlockPosition blockposition) {
        if (entity instanceof EntityPlayer && entity.level.getGameTime() % 20L == 0L) {
            CriterionTriggers.HONEY_BLOCK_SLIDE.trigger((EntityPlayer) entity, entity.level.getBlockState(blockposition));
        }

    }

    private void doSlideMovement(Entity entity) {
        Vec3D vec3d = entity.getDeltaMovement();

        if (vec3d.y < -0.13D) {
            double d0 = -0.05D / vec3d.y;

            entity.setDeltaMovement(new Vec3D(vec3d.x * d0, -0.05D, vec3d.z * d0));
        } else {
            entity.setDeltaMovement(new Vec3D(vec3d.x, -0.05D, vec3d.z));
        }

        entity.resetFallDistance();
    }

    private void maybeDoSlideEffects(World world, Entity entity) {
        if (doesEntityDoHoneyBlockSlideEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEffects.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (!world.isClientSide && world.random.nextInt(5) == 0) {
                world.broadcastEntityEvent(entity, (byte) 53);
            }
        }

    }

    public static void showSlideParticles(Entity entity) {
        showParticles(entity, 5);
    }

    public static void showJumpParticles(Entity entity) {
        showParticles(entity, 10);
    }

    private static void showParticles(Entity entity, int i) {
        if (entity.level.isClientSide) {
            IBlockData iblockdata = Blocks.HONEY_BLOCK.defaultBlockState();

            for (int j = 0; j < i; ++j) {
                entity.level.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
            }

        }
    }
}

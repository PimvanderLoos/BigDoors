package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;

public class EntityWitherSkull extends EntityFireball {

    private static final DataWatcherObject<Boolean> DATA_DANGEROUS = DataWatcher.defineId(EntityWitherSkull.class, DataWatcherRegistry.BOOLEAN);

    public EntityWitherSkull(EntityTypes<? extends EntityWitherSkull> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityWitherSkull(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(EntityTypes.WITHER_SKULL, entityliving, d0, d1, d2, world);
    }

    @Override
    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return this.isDangerous() && EntityWither.canDestroy(iblockdata) ? Math.min(0.8F, f) : f;
    }

    @Override
    protected void onHitEntity(MovingObjectPositionEntity movingobjectpositionentity) {
        super.onHitEntity(movingobjectpositionentity);
        if (!this.level.isClientSide) {
            Entity entity = movingobjectpositionentity.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;

            if (entity1 instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity1;

                flag = entity.hurt(DamageSource.witherSkull(this, entityliving), 8.0F);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(entityliving, entity);
                    } else {
                        entityliving.heal(5.0F);
                    }
                }
            } else {
                flag = entity.hurt(DamageSource.MAGIC, 5.0F);
            }

            if (flag && entity instanceof EntityLiving) {
                byte b0 = 0;

                if (this.level.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 10;
                } else if (this.level.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 40;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.WITHER, 20 * b0, 1), this.getEffectSource());
                }
            }

        }
    }

    @Override
    protected void onHit(MovingObjectPosition movingobjectposition) {
        super.onHit(movingobjectposition);
        if (!this.level.isClientSide) {
            Explosion.Effect explosion_effect = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;

            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, explosion_effect);
            this.discard();
        }

    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EntityWitherSkull.DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return (Boolean) this.entityData.get(EntityWitherSkull.DATA_DANGEROUS);
    }

    public void setDangerous(boolean flag) {
        this.entityData.set(EntityWitherSkull.DATA_DANGEROUS, flag);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }
}

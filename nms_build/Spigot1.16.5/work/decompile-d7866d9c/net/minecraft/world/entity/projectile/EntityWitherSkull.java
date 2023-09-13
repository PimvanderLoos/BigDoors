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

    private static final DataWatcherObject<Boolean> e = DataWatcher.a(EntityWitherSkull.class, DataWatcherRegistry.i);

    public EntityWitherSkull(EntityTypes<? extends EntityWitherSkull> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityWitherSkull(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(EntityTypes.WITHER_SKULL, entityliving, d0, d1, d2, world);
    }

    @Override
    protected float i() {
        return this.isCharged() ? 0.73F : super.i();
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return this.isCharged() && EntityWither.c(iblockdata) ? Math.min(0.8F, f) : f;
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        if (!this.world.isClientSide) {
            Entity entity = movingobjectpositionentity.getEntity();
            Entity entity1 = this.getShooter();
            boolean flag;

            if (entity1 instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity1;

                flag = entity.damageEntity(DamageSource.a(this, (Entity) entityliving), 8.0F);
                if (flag) {
                    if (entity.isAlive()) {
                        this.a(entityliving, entity);
                    } else {
                        entityliving.heal(5.0F);
                    }
                }
            } else {
                flag = entity.damageEntity(DamageSource.MAGIC, 5.0F);
            }

            if (flag && entity instanceof EntityLiving) {
                byte b0 = 0;

                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 10;
                } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 40;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.WITHER, 20 * b0, 1));
                }
            }

        }
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (!this.world.isClientSide) {
            Explosion.Effect explosion_effect = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;

            this.world.createExplosion(this, this.locX(), this.locY(), this.locZ(), 1.0F, false, explosion_effect);
            this.die();
        }

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityWitherSkull.e, false);
    }

    public boolean isCharged() {
        return (Boolean) this.datawatcher.get(EntityWitherSkull.e);
    }

    public void setCharged(boolean flag) {
        this.datawatcher.set(EntityWitherSkull.e, flag);
    }

    @Override
    protected boolean W_() {
        return false;
    }
}

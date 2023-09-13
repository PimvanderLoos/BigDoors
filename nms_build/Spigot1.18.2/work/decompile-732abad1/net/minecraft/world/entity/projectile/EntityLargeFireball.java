package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;

public class EntityLargeFireball extends EntityFireballFireball {

    public int explosionPower = 1;

    public EntityLargeFireball(EntityTypes<? extends EntityLargeFireball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityLargeFireball(World world, EntityLiving entityliving, double d0, double d1, double d2, int i) {
        super(EntityTypes.FIREBALL, entityliving, d0, d1, d2, world);
        this.explosionPower = i;
    }

    @Override
    protected void onHit(MovingObjectPosition movingobjectposition) {
        super.onHit(movingobjectposition);
        if (!this.level.isClientSide) {
            boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

            this.level.explode((Entity) null, this.getX(), this.getY(), this.getZ(), (float) this.explosionPower, flag, flag ? Explosion.Effect.DESTROY : Explosion.Effect.NONE);
            this.discard();
        }

    }

    @Override
    protected void onHitEntity(MovingObjectPositionEntity movingobjectpositionentity) {
        super.onHitEntity(movingobjectpositionentity);
        if (!this.level.isClientSide) {
            Entity entity = movingobjectpositionentity.getEntity();
            Entity entity1 = this.getOwner();

            entity.hurt(DamageSource.fireball(this, entity1), 6.0F);
            if (entity1 instanceof EntityLiving) {
                this.doEnchantDamageEffects((EntityLiving) entity1, entity);
            }

        }
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putByte("ExplosionPower", (byte) this.explosionPower);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("ExplosionPower", 99)) {
            this.explosionPower = nbttagcompound.getByte("ExplosionPower");
        }

    }
}

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
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (!this.level.isClientSide) {
            boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

            this.level.createExplosion((Entity) null, this.locX(), this.locY(), this.locZ(), (float) this.explosionPower, flag, flag ? Explosion.Effect.DESTROY : Explosion.Effect.NONE);
            this.die();
        }

    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        if (!this.level.isClientSide) {
            Entity entity = movingobjectpositionentity.getEntity();
            Entity entity1 = this.getShooter();

            entity.damageEntity(DamageSource.fireball(this, entity1), 6.0F);
            if (entity1 instanceof EntityLiving) {
                this.a((EntityLiving) entity1, entity);
            }

        }
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setByte("ExplosionPower", (byte) this.explosionPower);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("ExplosionPower", 99)) {
            this.explosionPower = nbttagcompound.getByte("ExplosionPower");
        }

    }
}

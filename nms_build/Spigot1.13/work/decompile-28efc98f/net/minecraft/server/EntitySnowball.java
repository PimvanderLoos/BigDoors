package net.minecraft.server;

public class EntitySnowball extends EntityProjectile {

    public EntitySnowball(World world) {
        super(EntityTypes.SNOWBALL, world);
    }

    public EntitySnowball(World world, EntityLiving entityliving) {
        super(EntityTypes.SNOWBALL, entityliving, world);
    }

    public EntitySnowball(World world, double d0, double d1, double d2) {
        super(EntityTypes.SNOWBALL, d0, d1, d2, world);
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.entity != null) {
            byte b0 = 0;

            if (movingobjectposition.entity instanceof EntityBlaze) {
                b0 = 3;
            }

            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), (float) b0);
        }

        if (!this.world.isClientSide) {
            this.world.broadcastEntityEffect(this, (byte) 3);
            this.die();
        }

    }
}

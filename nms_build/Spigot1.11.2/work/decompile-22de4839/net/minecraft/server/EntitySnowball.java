package net.minecraft.server;

public class EntitySnowball extends EntityProjectile {

    public EntitySnowball(World world) {
        super(world);
    }

    public EntitySnowball(World world, EntityLiving entityliving) {
        super(world, entityliving);
    }

    public EntitySnowball(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityProjectile.a(dataconvertermanager, "Snowball");
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

package net.minecraft.server;

public class EntityThrownExpBottle extends EntityProjectile {

    public EntityThrownExpBottle(World world) {
        super(world);
    }

    public EntityThrownExpBottle(World world, EntityLiving entityliving) {
        super(world, entityliving);
    }

    public EntityThrownExpBottle(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityProjectile.a(dataconvertermanager, "ThrowableExpBottle");
    }

    protected float j() {
        return 0.07F;
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            this.world.triggerEffect(2002, new BlockPosition(this), PotionUtil.a(Potions.b));
            int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5);

            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
            }

            this.die();
        }

    }
}

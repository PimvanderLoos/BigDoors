package net.minecraft.server;

public class EntityMinecartRideable extends EntityMinecartAbstract {

    public EntityMinecartRideable(World world) {
        super(EntityTypes.MINECART, world);
    }

    public EntityMinecartRideable(World world, double d0, double d1, double d2) {
        super(EntityTypes.MINECART, world, d0, d1, d2);
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (entityhuman.isSneaking()) {
            return false;
        } else if (this.isVehicle()) {
            return true;
        } else {
            if (!this.world.isClientSide) {
                entityhuman.startRiding(this);
            }

            return true;
        }
    }

    public void a(int i, int j, int k, boolean flag) {
        if (flag) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }

            if (this.getType() == 0) {
                this.k(-this.u());
                this.d(10);
                this.setDamage(50.0F);
                this.aA();
            }
        }

    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.RIDEABLE;
    }
}

package net.minecraft.server;

public abstract class EntityWaterAnimal extends EntityInsentient implements IAnimal {

    public EntityWaterAnimal(World world) {
        super(world);
    }

    public boolean bF() {
        return true;
    }

    public boolean cM() {
        return true;
    }

    public boolean canSpawn() {
        return this.world.a(this.getBoundingBox(), (Entity) this);
    }

    public int C() {
        return 120;
    }

    protected boolean isTypeNotPersistent() {
        return true;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    public void U() {
        int i = this.getAirTicks();

        super.U();
        if (this.isAlive() && !this.isInWater()) {
            --i;
            this.setAirTicks(i);
            if (this.getAirTicks() == -20) {
                this.setAirTicks(0);
                this.damageEntity(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirTicks(300);
        }

    }

    public boolean bg() {
        return false;
    }
}

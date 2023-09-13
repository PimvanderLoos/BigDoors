package net.minecraft.server;

public abstract class EntityWaterAnimal extends EntityCreature implements IAnimal {

    protected EntityWaterAnimal(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    public boolean ca() {
        return true;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.e;
    }

    public boolean a(IWorldReader iworldreader) {
        return iworldreader.a_(this, this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox());
    }

    public int z() {
        return 120;
    }

    public boolean isTypeNotPersistent() {
        return true;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    protected void a(int i) {
        if (this.isAlive() && !this.aq()) {
            this.setAirTicks(i - 1);
            if (this.getAirTicks() == -20) {
                this.setAirTicks(0);
                this.damageEntity(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirTicks(300);
        }

    }

    public void W() {
        int i = this.getAirTicks();

        super.W();
        this.a(i);
    }

    public boolean bw() {
        return false;
    }

    public boolean a(EntityHuman entityhuman) {
        return false;
    }
}

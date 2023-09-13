package net.minecraft.server;

public abstract class EntityPerchable extends EntityTameableAnimal {

    private int bG;

    protected EntityPerchable(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    public boolean g(EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("id", this.getSaveID());
        this.save(nbttagcompound);
        if (entityhuman.g(nbttagcompound)) {
            this.world.kill(this);
            return true;
        } else {
            return false;
        }
    }

    public void tick() {
        ++this.bG;
        super.tick();
    }

    public boolean dK() {
        return this.bG > 100;
    }
}

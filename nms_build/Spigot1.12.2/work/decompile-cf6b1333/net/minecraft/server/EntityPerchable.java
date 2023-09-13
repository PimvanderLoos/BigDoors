package net.minecraft.server;

public abstract class EntityPerchable extends EntityTameableAnimal {

    private int bB;

    public EntityPerchable(World world) {
        super(world);
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

    public void B_() {
        ++this.bB;
        super.B_();
    }

    public boolean dw() {
        return this.bB > 100;
    }
}

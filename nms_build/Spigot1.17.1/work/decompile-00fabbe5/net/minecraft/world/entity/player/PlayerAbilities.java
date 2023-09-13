package net.minecraft.world.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerAbilities {

    public boolean invulnerable;
    public boolean flying;
    public boolean mayfly;
    public boolean instabuild;
    public boolean mayBuild = true;
    public float flyingSpeed = 0.05F;
    public float walkingSpeed = 0.1F;

    public PlayerAbilities() {}

    public void a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.setBoolean("invulnerable", this.invulnerable);
        nbttagcompound1.setBoolean("flying", this.flying);
        nbttagcompound1.setBoolean("mayfly", this.mayfly);
        nbttagcompound1.setBoolean("instabuild", this.instabuild);
        nbttagcompound1.setBoolean("mayBuild", this.mayBuild);
        nbttagcompound1.setFloat("flySpeed", this.flyingSpeed);
        nbttagcompound1.setFloat("walkSpeed", this.walkingSpeed);
        nbttagcompound.set("abilities", nbttagcompound1);
    }

    public void b(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("abilities", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("abilities");

            this.invulnerable = nbttagcompound1.getBoolean("invulnerable");
            this.flying = nbttagcompound1.getBoolean("flying");
            this.mayfly = nbttagcompound1.getBoolean("mayfly");
            this.instabuild = nbttagcompound1.getBoolean("instabuild");
            if (nbttagcompound1.hasKeyOfType("flySpeed", 99)) {
                this.flyingSpeed = nbttagcompound1.getFloat("flySpeed");
                this.walkingSpeed = nbttagcompound1.getFloat("walkSpeed");
            }

            if (nbttagcompound1.hasKeyOfType("mayBuild", 1)) {
                this.mayBuild = nbttagcompound1.getBoolean("mayBuild");
            }
        }

    }

    public float a() {
        return this.flyingSpeed;
    }

    public void a(float f) {
        this.flyingSpeed = f;
    }

    public float b() {
        return this.walkingSpeed;
    }

    public void b(float f) {
        this.walkingSpeed = f;
    }
}

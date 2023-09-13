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

    public void addSaveData(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.putBoolean("invulnerable", this.invulnerable);
        nbttagcompound1.putBoolean("flying", this.flying);
        nbttagcompound1.putBoolean("mayfly", this.mayfly);
        nbttagcompound1.putBoolean("instabuild", this.instabuild);
        nbttagcompound1.putBoolean("mayBuild", this.mayBuild);
        nbttagcompound1.putFloat("flySpeed", this.flyingSpeed);
        nbttagcompound1.putFloat("walkSpeed", this.walkingSpeed);
        nbttagcompound.put("abilities", nbttagcompound1);
    }

    public void loadSaveData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.contains("abilities", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("abilities");

            this.invulnerable = nbttagcompound1.getBoolean("invulnerable");
            this.flying = nbttagcompound1.getBoolean("flying");
            this.mayfly = nbttagcompound1.getBoolean("mayfly");
            this.instabuild = nbttagcompound1.getBoolean("instabuild");
            if (nbttagcompound1.contains("flySpeed", 99)) {
                this.flyingSpeed = nbttagcompound1.getFloat("flySpeed");
                this.walkingSpeed = nbttagcompound1.getFloat("walkSpeed");
            }

            if (nbttagcompound1.contains("mayBuild", 1)) {
                this.mayBuild = nbttagcompound1.getBoolean("mayBuild");
            }
        }

    }

    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    public void setFlyingSpeed(float f) {
        this.flyingSpeed = f;
    }

    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }

    public void setWalkingSpeed(float f) {
        this.walkingSpeed = f;
    }
}

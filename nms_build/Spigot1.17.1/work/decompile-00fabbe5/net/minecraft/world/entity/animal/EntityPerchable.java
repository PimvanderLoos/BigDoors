package net.minecraft.world.entity.animal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;

public abstract class EntityPerchable extends EntityTameableAnimal {

    private static final int RIDE_COOLDOWN = 100;
    private int rideCooldownCounter;

    protected EntityPerchable(EntityTypes<? extends EntityPerchable> entitytypes, World world) {
        super(entitytypes, world);
    }

    public boolean b(EntityPlayer entityplayer) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("id", this.getSaveID());
        this.save(nbttagcompound);
        if (entityplayer.h(nbttagcompound)) {
            this.die();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void tick() {
        ++this.rideCooldownCounter;
        super.tick();
    }

    public boolean fH() {
        return this.rideCooldownCounter > 100;
    }
}

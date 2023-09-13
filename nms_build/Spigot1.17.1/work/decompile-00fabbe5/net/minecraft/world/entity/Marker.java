package net.minecraft.world.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.World;

public class Marker extends Entity {

    private static final String DATA_TAG = "data";
    private NBTTagCompound data = new NBTTagCompound();

    public Marker(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.noPhysics = true;
    }

    @Override
    public void tick() {}

    @Override
    protected void initDatawatcher() {}

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        this.data = nbttagcompound.getCompound("data");
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.set("data", this.data.clone());
    }

    @Override
    public Packet<?> getPacket() {
        throw new IllegalStateException("Markers should never be sent");
    }

    @Override
    protected void addPassenger(Entity entity) {
        entity.stopRiding();
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayInVehicleMove implements Packet<PacketListenerPlayIn> {

    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;

    public PacketPlayInVehicleMove(Entity entity) {
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.yRot = entity.getYRot();
        this.xRot = entity.getXRot();
    }

    public PacketPlayInVehicleMove(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readFloat();
        this.xRot = packetdataserializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.yRot);
        packetdataserializer.writeFloat(this.xRot);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleMoveVehicle(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }
}

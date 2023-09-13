package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutVehicleMove implements Packet<PacketListenerPlayOut> {

    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;

    public PacketPlayOutVehicleMove(Entity entity) {
        this.x = entity.locX();
        this.y = entity.locY();
        this.z = entity.locZ();
        this.yRot = entity.getYRot();
        this.xRot = entity.getXRot();
    }

    public PacketPlayOutVehicleMove(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readDouble();
        this.y = packetdataserializer.readDouble();
        this.z = packetdataserializer.readDouble();
        this.yRot = packetdataserializer.readFloat();
        this.xRot = packetdataserializer.readFloat();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.x);
        packetdataserializer.writeDouble(this.y);
        packetdataserializer.writeDouble(this.z);
        packetdataserializer.writeFloat(this.yRot);
        packetdataserializer.writeFloat(this.xRot);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public double b() {
        return this.x;
    }

    public double c() {
        return this.y;
    }

    public double d() {
        return this.z;
    }

    public float e() {
        return this.yRot;
    }

    public float f() {
        return this.xRot;
    }
}

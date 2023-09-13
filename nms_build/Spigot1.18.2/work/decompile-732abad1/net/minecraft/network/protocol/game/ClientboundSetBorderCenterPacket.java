package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderCenterPacket implements Packet<PacketListenerPlayOut> {

    private final double newCenterX;
    private final double newCenterZ;

    public ClientboundSetBorderCenterPacket(WorldBorder worldborder) {
        this.newCenterX = worldborder.getCenterX();
        this.newCenterZ = worldborder.getCenterZ();
    }

    public ClientboundSetBorderCenterPacket(PacketDataSerializer packetdataserializer) {
        this.newCenterX = packetdataserializer.readDouble();
        this.newCenterZ = packetdataserializer.readDouble();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.newCenterX);
        packetdataserializer.writeDouble(this.newCenterZ);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetBorderCenter(this);
    }

    public double getNewCenterZ() {
        return this.newCenterZ;
    }

    public double getNewCenterX() {
        return this.newCenterX;
    }
}

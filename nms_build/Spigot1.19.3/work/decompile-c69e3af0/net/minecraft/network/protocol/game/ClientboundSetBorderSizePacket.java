package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket implements Packet<PacketListenerPlayOut> {

    private final double size;

    public ClientboundSetBorderSizePacket(WorldBorder worldborder) {
        this.size = worldborder.getLerpTarget();
    }

    public ClientboundSetBorderSizePacket(PacketDataSerializer packetdataserializer) {
        this.size = packetdataserializer.readDouble();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.size);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetBorderSize(this);
    }

    public double getSize() {
        return this.size;
    }
}

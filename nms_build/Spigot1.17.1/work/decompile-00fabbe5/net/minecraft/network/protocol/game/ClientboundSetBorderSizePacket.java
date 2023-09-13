package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket implements Packet<PacketListenerPlayOut> {

    private final double size;

    public ClientboundSetBorderSizePacket(WorldBorder worldborder) {
        this.size = worldborder.k();
    }

    public ClientboundSetBorderSizePacket(PacketDataSerializer packetdataserializer) {
        this.size = packetdataserializer.readDouble();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeDouble(this.size);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public double b() {
        return this.size;
    }
}

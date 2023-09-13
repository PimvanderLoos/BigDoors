package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutViewDistance implements Packet<PacketListenerPlayOut> {

    private final int radius;

    public PacketPlayOutViewDistance(int i) {
        this.radius = i;
    }

    public PacketPlayOutViewDistance(PacketDataSerializer packetdataserializer) {
        this.radius = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.radius);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetChunkCacheRadius(this);
    }

    public int getRadius() {
        return this.radius;
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutKeepAlive implements Packet<PacketListenerPlayOut> {

    private final long id;

    public PacketPlayOutKeepAlive(long i) {
        this.id = i;
    }

    public PacketPlayOutKeepAlive(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readLong();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.id);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleKeepAlive(this);
    }

    public long getId() {
        return this.id;
    }
}

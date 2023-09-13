package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInKeepAlive implements Packet<PacketListenerPlayIn> {

    private final long id;

    public PacketPlayInKeepAlive(long i) {
        this.id = i;
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleKeepAlive(this);
    }

    public PacketPlayInKeepAlive(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readLong();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}

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
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.id);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public long b() {
        return this.id;
    }
}

package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutKeepAlive implements Packet<PacketListenerPlayOut> {

    private long a;

    public PacketPlayOutKeepAlive() {}

    public PacketPlayOutKeepAlive(long i) {
        this.a = i;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readLong();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeLong(this.a);
    }
}

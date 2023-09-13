package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutViewDistance implements Packet<PacketListenerPlayOut> {

    private final int radius;

    public PacketPlayOutViewDistance(int i) {
        this.radius = i;
    }

    public PacketPlayOutViewDistance(PacketDataSerializer packetdataserializer) {
        this.radius = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.radius);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.radius;
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutViewCentre implements Packet<PacketListenerPlayOut> {

    private final int x;
    private final int z;

    public PacketPlayOutViewCentre(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public PacketPlayOutViewCentre(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.j();
        this.z = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.x);
        packetdataserializer.d(this.z);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.x;
    }

    public int c() {
        return this.z;
    }
}

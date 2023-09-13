package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInBeacon implements Packet<PacketListenerPlayIn> {

    private final int primary;
    private final int secondary;

    public PacketPlayInBeacon(int i, int j) {
        this.primary = i;
        this.secondary = j;
    }

    public PacketPlayInBeacon(PacketDataSerializer packetdataserializer) {
        this.primary = packetdataserializer.j();
        this.secondary = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.primary);
        packetdataserializer.d(this.secondary);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.primary;
    }

    public int c() {
        return this.secondary;
    }
}

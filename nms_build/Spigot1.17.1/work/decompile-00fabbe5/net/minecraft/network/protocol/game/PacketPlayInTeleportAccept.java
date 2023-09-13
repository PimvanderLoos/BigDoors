package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTeleportAccept implements Packet<PacketListenerPlayIn> {

    private final int id;

    public PacketPlayInTeleportAccept(int i) {
        this.id = i;
    }

    public PacketPlayInTeleportAccept(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.id;
    }
}

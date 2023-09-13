package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTrSel implements Packet<PacketListenerPlayIn> {

    private final int item;

    public PacketPlayInTrSel(int i) {
        this.item = i;
    }

    public PacketPlayInTrSel(PacketDataSerializer packetdataserializer) {
        this.item = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.item);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.item;
    }
}

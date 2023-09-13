package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInPickItem implements Packet<PacketListenerPlayIn> {

    private final int slot;

    public PacketPlayInPickItem(int i) {
        this.slot = i;
    }

    public PacketPlayInPickItem(PacketDataSerializer packetdataserializer) {
        this.slot = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.slot);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.slot;
    }
}

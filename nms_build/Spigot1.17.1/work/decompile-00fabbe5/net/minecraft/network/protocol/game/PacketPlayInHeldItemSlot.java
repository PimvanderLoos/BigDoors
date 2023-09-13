package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInHeldItemSlot implements Packet<PacketListenerPlayIn> {

    private final int slot;

    public PacketPlayInHeldItemSlot(int i) {
        this.slot = i;
    }

    public PacketPlayInHeldItemSlot(PacketDataSerializer packetdataserializer) {
        this.slot = packetdataserializer.readShort();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeShort(this.slot);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.slot;
    }
}

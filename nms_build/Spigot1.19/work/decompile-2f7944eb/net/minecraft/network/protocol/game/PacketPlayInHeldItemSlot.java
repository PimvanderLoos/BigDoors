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
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeShort(this.slot);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSetCarriedItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}

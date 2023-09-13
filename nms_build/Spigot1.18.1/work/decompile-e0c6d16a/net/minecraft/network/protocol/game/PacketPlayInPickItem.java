package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInPickItem implements Packet<PacketListenerPlayIn> {

    private final int slot;

    public PacketPlayInPickItem(int i) {
        this.slot = i;
    }

    public PacketPlayInPickItem(PacketDataSerializer packetdataserializer) {
        this.slot = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.slot);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handlePickItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}

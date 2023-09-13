package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTrSel implements Packet<PacketListenerPlayIn> {

    private final int item;

    public PacketPlayInTrSel(int i) {
        this.item = i;
    }

    public PacketPlayInTrSel(PacketDataSerializer packetdataserializer) {
        this.item = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.item);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSelectTrade(this);
    }

    public int getItem() {
        return this.item;
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInItemName implements Packet<PacketListenerPlayIn> {

    private final String name;

    public PacketPlayInItemName(String s) {
        this.name = s;
    }

    public PacketPlayInItemName(PacketDataSerializer packetdataserializer) {
        this.name = packetdataserializer.readUtf();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.name);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleRenameItem(this);
    }

    public String getName() {
        return this.name;
    }
}

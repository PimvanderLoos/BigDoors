package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTabComplete implements Packet<PacketListenerPlayIn> {

    private final int id;
    private final String command;

    public PacketPlayInTabComplete(int i, String s) {
        this.id = i;
        this.command = s;
    }

    public PacketPlayInTabComplete(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
        this.command = packetdataserializer.readUtf(32500);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeUtf(this.command, 32500);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleCustomCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public String getCommand() {
        return this.command;
    }
}

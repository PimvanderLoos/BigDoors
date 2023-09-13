package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTeleportAccept implements Packet<PacketListenerPlayIn> {

    private final int id;

    public PacketPlayInTeleportAccept(int i) {
        this.id = i;
    }

    public PacketPlayInTeleportAccept(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleAcceptTeleportPacket(this);
    }

    public int getId() {
        return this.id;
    }
}

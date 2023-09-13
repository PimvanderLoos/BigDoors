package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class ServerboundPongPacket implements Packet<PacketListenerPlayIn> {

    private final int id;

    public ServerboundPongPacket(int i) {
        this.id = i;
    }

    public ServerboundPongPacket(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.id);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handlePong(this);
    }

    public int getId() {
        return this.id;
    }
}

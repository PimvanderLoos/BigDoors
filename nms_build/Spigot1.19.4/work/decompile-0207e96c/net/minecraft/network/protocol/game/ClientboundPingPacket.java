package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class ClientboundPingPacket implements Packet<PacketListenerPlayOut> {

    private final int id;

    public ClientboundPingPacket(int i) {
        this.id = i;
    }

    public ClientboundPingPacket(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.id);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePing(this);
    }

    public int getId() {
        return this.id;
    }
}

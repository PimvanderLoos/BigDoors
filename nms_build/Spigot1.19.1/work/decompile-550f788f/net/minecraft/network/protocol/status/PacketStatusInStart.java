package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketStatusInStart implements Packet<PacketStatusInListener> {

    public PacketStatusInStart() {}

    public PacketStatusInStart(PacketDataSerializer packetdataserializer) {}

    @Override
    public void write(PacketDataSerializer packetdataserializer) {}

    public void handle(PacketStatusInListener packetstatusinlistener) {
        packetstatusinlistener.handleStatusRequest(this);
    }
}

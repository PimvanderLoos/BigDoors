package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketStatusInStart implements Packet<PacketStatusInListener> {

    public PacketStatusInStart() {}

    public PacketStatusInStart(PacketDataSerializer packetdataserializer) {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) {}

    public void a(PacketStatusInListener packetstatusinlistener) {
        packetstatusinlistener.a(this);
    }
}

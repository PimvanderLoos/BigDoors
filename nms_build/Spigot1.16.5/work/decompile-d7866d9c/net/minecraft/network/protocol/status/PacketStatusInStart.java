package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketStatusInStart implements Packet<PacketStatusInListener> {

    public PacketStatusInStart() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {}

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {}

    public void a(PacketStatusInListener packetstatusinlistener) {
        packetstatusinlistener.a(this);
    }
}

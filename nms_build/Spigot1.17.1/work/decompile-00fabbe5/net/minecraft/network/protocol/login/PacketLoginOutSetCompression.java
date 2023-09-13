package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutSetCompression implements Packet<PacketLoginOutListener> {

    private final int compressionThreshold;

    public PacketLoginOutSetCompression(int i) {
        this.compressionThreshold = i;
    }

    public PacketLoginOutSetCompression(PacketDataSerializer packetdataserializer) {
        this.compressionThreshold = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.compressionThreshold);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }

    public int b() {
        return this.compressionThreshold;
    }
}

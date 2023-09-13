package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutSetCompression implements Packet<PacketLoginOutListener> {

    private final int compressionThreshold;

    public PacketLoginOutSetCompression(int i) {
        this.compressionThreshold = i;
    }

    public PacketLoginOutSetCompression(PacketDataSerializer packetdataserializer) {
        this.compressionThreshold = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.compressionThreshold);
    }

    public void handle(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.handleCompression(this);
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}

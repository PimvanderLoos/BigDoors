package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketStatusInPing implements Packet<PacketStatusInListener> {

    private final long time;

    public PacketStatusInPing(long i) {
        this.time = i;
    }

    public PacketStatusInPing(PacketDataSerializer packetdataserializer) {
        this.time = packetdataserializer.readLong();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.time);
    }

    public void handle(PacketStatusInListener packetstatusinlistener) {
        packetstatusinlistener.handlePingRequest(this);
    }

    public long getTime() {
        return this.time;
    }
}

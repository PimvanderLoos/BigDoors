package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketStatusOutPong implements Packet<PacketStatusOutListener> {

    private final long time;

    public PacketStatusOutPong(long i) {
        this.time = i;
    }

    public PacketStatusOutPong(PacketDataSerializer packetdataserializer) {
        this.time = packetdataserializer.readLong();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.time);
    }

    public void a(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.a(this);
    }

    public long b() {
        return this.time;
    }
}

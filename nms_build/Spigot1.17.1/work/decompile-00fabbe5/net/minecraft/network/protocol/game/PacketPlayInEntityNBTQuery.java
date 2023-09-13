package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInEntityNBTQuery implements Packet<PacketListenerPlayIn> {

    private final int transactionId;
    private final int entityId;

    public PacketPlayInEntityNBTQuery(int i, int j) {
        this.transactionId = i;
        this.entityId = j;
    }

    public PacketPlayInEntityNBTQuery(PacketDataSerializer packetdataserializer) {
        this.transactionId = packetdataserializer.j();
        this.entityId = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.transactionId);
        packetdataserializer.d(this.entityId);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.transactionId;
    }

    public int c() {
        return this.entityId;
    }
}

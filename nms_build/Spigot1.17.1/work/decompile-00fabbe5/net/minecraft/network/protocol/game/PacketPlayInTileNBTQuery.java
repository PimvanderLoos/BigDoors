package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInTileNBTQuery implements Packet<PacketListenerPlayIn> {

    private final int transactionId;
    private final BlockPosition pos;

    public PacketPlayInTileNBTQuery(int i, BlockPosition blockposition) {
        this.transactionId = i;
        this.pos = blockposition;
    }

    public PacketPlayInTileNBTQuery(PacketDataSerializer packetdataserializer) {
        this.transactionId = packetdataserializer.j();
        this.pos = packetdataserializer.f();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.transactionId);
        packetdataserializer.a(this.pos);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int b() {
        return this.transactionId;
    }

    public BlockPosition c() {
        return this.pos;
    }
}

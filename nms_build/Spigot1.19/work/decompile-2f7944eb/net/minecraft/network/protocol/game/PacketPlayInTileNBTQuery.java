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
        this.transactionId = packetdataserializer.readVarInt();
        this.pos = packetdataserializer.readBlockPos();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.transactionId);
        packetdataserializer.writeBlockPos(this.pos);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleBlockEntityTagQuery(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public BlockPosition getPos() {
        return this.pos;
    }
}

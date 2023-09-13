package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutCollect implements Packet<PacketListenerPlayOut> {

    private final int itemId;
    private final int playerId;
    private final int amount;

    public PacketPlayOutCollect(int i, int j, int k) {
        this.itemId = i;
        this.playerId = j;
        this.amount = k;
    }

    public PacketPlayOutCollect(PacketDataSerializer packetdataserializer) {
        this.itemId = packetdataserializer.readVarInt();
        this.playerId = packetdataserializer.readVarInt();
        this.amount = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.itemId);
        packetdataserializer.writeVarInt(this.playerId);
        packetdataserializer.writeVarInt(this.amount);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleTakeItemEntity(this);
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getAmount() {
        return this.amount;
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutOpenWindowHorse implements Packet<PacketListenerPlayOut> {

    private final int containerId;
    private final int size;
    private final int entityId;

    public PacketPlayOutOpenWindowHorse(int i, int j, int k) {
        this.containerId = i;
        this.size = j;
        this.entityId = k;
    }

    public PacketPlayOutOpenWindowHorse(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readUnsignedByte();
        this.size = packetdataserializer.readVarInt();
        this.entityId = packetdataserializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeVarInt(this.size);
        packetdataserializer.writeInt(this.entityId);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleHorseScreenOpen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSize() {
        return this.size;
    }

    public int getEntityId() {
        return this.entityId;
    }
}

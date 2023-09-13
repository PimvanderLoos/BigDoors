package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutWindowData implements Packet<PacketListenerPlayOut> {

    private final int containerId;
    private final int id;
    private final int value;

    public PacketPlayOutWindowData(int i, int j, int k) {
        this.containerId = i;
        this.id = j;
        this.value = k;
    }

    public PacketPlayOutWindowData(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readUnsignedByte();
        this.id = packetdataserializer.readShort();
        this.value = packetdataserializer.readShort();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeShort(this.id);
        packetdataserializer.writeShort(this.value);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleContainerSetData(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getId() {
        return this.id;
    }

    public int getValue() {
        return this.value;
    }
}

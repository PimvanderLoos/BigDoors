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
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeShort(this.id);
        packetdataserializer.writeShort(this.value);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.containerId;
    }

    public int c() {
        return this.id;
    }

    public int d() {
        return this.value;
    }
}

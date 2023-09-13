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
        this.size = packetdataserializer.j();
        this.entityId = packetdataserializer.readInt();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.d(this.size);
        packetdataserializer.writeInt(this.entityId);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.containerId;
    }

    public int c() {
        return this.size;
    }

    public int d() {
        return this.entityId;
    }
}

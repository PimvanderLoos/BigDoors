package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutUnloadChunk implements Packet<PacketListenerPlayOut> {

    private final int x;
    private final int z;

    public PacketPlayOutUnloadChunk(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public PacketPlayOutUnloadChunk(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.z);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleForgetLevelChunk(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}

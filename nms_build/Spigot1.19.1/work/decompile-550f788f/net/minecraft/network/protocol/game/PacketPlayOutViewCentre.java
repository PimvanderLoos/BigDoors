package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutViewCentre implements Packet<PacketListenerPlayOut> {

    private final int x;
    private final int z;

    public PacketPlayOutViewCentre(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public PacketPlayOutViewCentre(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readVarInt();
        this.z = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.x);
        packetdataserializer.writeVarInt(this.z);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetChunkCacheCenter(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInBeacon implements Packet<PacketListenerPlayIn> {

    private final int primary;
    private final int secondary;

    public PacketPlayInBeacon(int i, int j) {
        this.primary = i;
        this.secondary = j;
    }

    public PacketPlayInBeacon(PacketDataSerializer packetdataserializer) {
        this.primary = packetdataserializer.readVarInt();
        this.secondary = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.primary);
        packetdataserializer.writeVarInt(this.secondary);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSetBeaconPacket(this);
    }

    public int getPrimary() {
        return this.primary;
    }

    public int getSecondary() {
        return this.secondary;
    }
}

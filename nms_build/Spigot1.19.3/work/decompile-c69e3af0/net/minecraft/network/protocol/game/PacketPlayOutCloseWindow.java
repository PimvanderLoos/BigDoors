package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutCloseWindow implements Packet<PacketListenerPlayOut> {

    private final int containerId;

    public PacketPlayOutCloseWindow(int i) {
        this.containerId = i;
    }

    public PacketPlayOutCloseWindow(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readUnsignedByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleContainerClose(this);
    }

    public int getContainerId() {
        return this.containerId;
    }
}

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
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.containerId;
    }
}

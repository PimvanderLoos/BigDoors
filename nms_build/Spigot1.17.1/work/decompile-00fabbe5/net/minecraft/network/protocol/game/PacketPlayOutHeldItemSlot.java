package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutHeldItemSlot implements Packet<PacketListenerPlayOut> {

    private final int slot;

    public PacketPlayOutHeldItemSlot(int i) {
        this.slot = i;
    }

    public PacketPlayOutHeldItemSlot(PacketDataSerializer packetdataserializer) {
        this.slot = packetdataserializer.readByte();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.slot);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.slot;
    }
}

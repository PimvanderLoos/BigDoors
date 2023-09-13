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
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.slot);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetCarriedItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}

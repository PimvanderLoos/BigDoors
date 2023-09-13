package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInEnchantItem implements Packet<PacketListenerPlayIn> {

    private final int containerId;
    private final int buttonId;

    public PacketPlayInEnchantItem(int i, int j) {
        this.containerId = i;
        this.buttonId = j;
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInEnchantItem(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.buttonId = packetdataserializer.readByte();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeByte(this.buttonId);
    }

    public int b() {
        return this.containerId;
    }

    public int c() {
        return this.buttonId;
    }
}

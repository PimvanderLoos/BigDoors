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

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleContainerButtonClick(this);
    }

    public PacketPlayInEnchantItem(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.buttonId = packetdataserializer.readByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeByte(this.buttonId);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getButtonId() {
        return this.buttonId;
    }
}

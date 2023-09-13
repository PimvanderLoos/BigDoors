package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDelayPacket implements Packet<PacketListenerPlayOut> {

    private final int warningDelay;

    public ClientboundSetBorderWarningDelayPacket(WorldBorder worldborder) {
        this.warningDelay = worldborder.getWarningTime();
    }

    public ClientboundSetBorderWarningDelayPacket(PacketDataSerializer packetdataserializer) {
        this.warningDelay = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.warningDelay);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetBorderWarningDelay(this);
    }

    public int getWarningDelay() {
        return this.warningDelay;
    }
}

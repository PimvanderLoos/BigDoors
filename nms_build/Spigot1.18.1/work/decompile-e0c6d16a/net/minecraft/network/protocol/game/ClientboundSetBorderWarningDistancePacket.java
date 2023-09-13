package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDistancePacket implements Packet<PacketListenerPlayOut> {

    private final int warningBlocks;

    public ClientboundSetBorderWarningDistancePacket(WorldBorder worldborder) {
        this.warningBlocks = worldborder.getWarningBlocks();
    }

    public ClientboundSetBorderWarningDistancePacket(PacketDataSerializer packetdataserializer) {
        this.warningBlocks = packetdataserializer.readVarInt();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.warningBlocks);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSetBorderWarningDistance(this);
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }
}

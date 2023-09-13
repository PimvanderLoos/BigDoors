package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDistancePacket implements Packet<PacketListenerPlayOut> {

    private final int warningBlocks;

    public ClientboundSetBorderWarningDistancePacket(WorldBorder worldborder) {
        this.warningBlocks = worldborder.getWarningDistance();
    }

    public ClientboundSetBorderWarningDistancePacket(PacketDataSerializer packetdataserializer) {
        this.warningBlocks = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.warningBlocks);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.warningBlocks;
    }
}

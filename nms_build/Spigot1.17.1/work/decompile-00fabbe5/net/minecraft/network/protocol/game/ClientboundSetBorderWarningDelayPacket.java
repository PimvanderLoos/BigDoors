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
        this.warningDelay = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.warningDelay);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.warningDelay;
    }
}

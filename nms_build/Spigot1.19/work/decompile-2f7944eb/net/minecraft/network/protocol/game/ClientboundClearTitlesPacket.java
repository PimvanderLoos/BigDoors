package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class ClientboundClearTitlesPacket implements Packet<PacketListenerPlayOut> {

    private final boolean resetTimes;

    public ClientboundClearTitlesPacket(boolean flag) {
        this.resetTimes = flag;
    }

    public ClientboundClearTitlesPacket(PacketDataSerializer packetdataserializer) {
        this.resetTimes = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.resetTimes);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleTitlesClear(this);
    }

    public boolean shouldResetTimes() {
        return this.resetTimes;
    }
}

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
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.resetTimes);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean b() {
        return this.resetTimes;
    }
}

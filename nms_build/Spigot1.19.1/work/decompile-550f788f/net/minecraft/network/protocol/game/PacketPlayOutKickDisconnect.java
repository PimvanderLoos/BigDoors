package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutKickDisconnect implements Packet<PacketListenerPlayOut> {

    private final IChatBaseComponent reason;

    public PacketPlayOutKickDisconnect(IChatBaseComponent ichatbasecomponent) {
        this.reason = ichatbasecomponent;
    }

    public PacketPlayOutKickDisconnect(PacketDataSerializer packetdataserializer) {
        this.reason = packetdataserializer.readComponent();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.reason);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleDisconnect(this);
    }

    public IChatBaseComponent getReason() {
        return this.reason;
    }
}

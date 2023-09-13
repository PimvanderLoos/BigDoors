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
        this.reason = packetdataserializer.i();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.reason);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public IChatBaseComponent b() {
        return this.reason;
    }
}

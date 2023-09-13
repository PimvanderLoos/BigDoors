package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutDisconnect implements Packet<PacketLoginOutListener> {

    private final IChatBaseComponent reason;

    public PacketLoginOutDisconnect(IChatBaseComponent ichatbasecomponent) {
        this.reason = ichatbasecomponent;
    }

    public PacketLoginOutDisconnect(PacketDataSerializer packetdataserializer) {
        this.reason = IChatBaseComponent.ChatSerializer.b(packetdataserializer.e(262144));
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.reason);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }

    public IChatBaseComponent b() {
        return this.reason;
    }
}

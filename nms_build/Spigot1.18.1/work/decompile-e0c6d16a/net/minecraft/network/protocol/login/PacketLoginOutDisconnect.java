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
        this.reason = IChatBaseComponent.ChatSerializer.fromJsonLenient(packetdataserializer.readUtf(262144));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.reason);
    }

    public void handle(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.handleDisconnect(this);
    }

    public IChatBaseComponent getReason() {
        return this.reason;
    }
}

package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ClientboundDeleteChatPacket(MessageSignature.a messageSignature) implements Packet<PacketListenerPlayOut> {

    public ClientboundDeleteChatPacket(PacketDataSerializer packetdataserializer) {
        this(MessageSignature.a.read(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        MessageSignature.a.write(packetdataserializer, this.messageSignature);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleDeleteChat(this);
    }
}

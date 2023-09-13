package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record ClientboundDeleteChatPacket(MessageSignature messageSignature) implements Packet<PacketListenerPlayOut> {

    public ClientboundDeleteChatPacket(PacketDataSerializer packetdataserializer) {
        this(new MessageSignature(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        this.messageSignature.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleDeleteChat(this);
    }
}

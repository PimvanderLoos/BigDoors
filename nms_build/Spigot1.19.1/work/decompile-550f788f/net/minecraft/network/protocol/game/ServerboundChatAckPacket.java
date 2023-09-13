package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatAckPacket(LastSeenMessages.b lastSeenMessages) implements Packet<PacketListenerPlayIn> {

    public ServerboundChatAckPacket(PacketDataSerializer packetdataserializer) {
        this(new LastSeenMessages.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        this.lastSeenMessages.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChatAck(this);
    }
}

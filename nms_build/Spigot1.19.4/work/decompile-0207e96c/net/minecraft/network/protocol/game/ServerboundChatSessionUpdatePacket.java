package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.a chatSession) implements Packet<PacketListenerPlayIn> {

    public ServerboundChatSessionUpdatePacket(PacketDataSerializer packetdataserializer) {
        this(RemoteChatSession.a.read(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        RemoteChatSession.a.write(packetdataserializer, this.chatSession);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChatSessionUpdate(this);
    }
}

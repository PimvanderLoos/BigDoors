package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public record ClientboundDisguisedChatPacket(IChatBaseComponent message, ChatMessageType.b chatType) implements Packet<PacketListenerPlayOut> {

    public ClientboundDisguisedChatPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readComponent(), new ChatMessageType.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.message);
        this.chatType.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleDisguisedChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}

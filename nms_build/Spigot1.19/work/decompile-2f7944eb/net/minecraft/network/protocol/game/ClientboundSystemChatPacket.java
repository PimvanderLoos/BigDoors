package net.minecraft.network.protocol.game;

import java.util.Objects;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;

public record ClientboundSystemChatPacket(IChatBaseComponent content, int typeId) implements Packet<PacketListenerPlayOut> {

    public ClientboundSystemChatPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readComponent(), packetdataserializer.readVarInt());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.content);
        packetdataserializer.writeVarInt(this.typeId);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleSystemChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public ChatMessageType resolveType(IRegistry<ChatMessageType> iregistry) {
        return (ChatMessageType) Objects.requireNonNull((ChatMessageType) iregistry.byId(this.typeId), "Invalid chat type");
    }
}

package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatPacket(PlayerChatMessage message, ChatMessageType.b chatType) implements Packet<PacketListenerPlayOut> {

    public ClientboundPlayerChatPacket(PacketDataSerializer packetdataserializer) {
        this(new PlayerChatMessage(packetdataserializer), new ChatMessageType.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        this.message.write(packetdataserializer);
        this.chatType.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public Optional<ChatMessageType.a> resolveChatType(IRegistryCustom iregistrycustom) {
        return this.chatType.resolve(iregistrycustom);
    }
}

package net.minecraft.network.protocol.game;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MinecraftEncryption;

public record ClientboundPlayerChatPacket(IChatBaseComponent signedContent, Optional<IChatBaseComponent> unsignedContent, int typeId, ChatSender sender, Instant timeStamp, MinecraftEncryption.b saltSignature) implements Packet<PacketListenerPlayOut> {

    private static final Duration MESSAGE_EXPIRES_AFTER = PacketPlayInChat.MESSAGE_EXPIRES_AFTER.plus(Duration.ofMinutes(2L));

    public ClientboundPlayerChatPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readComponent(), packetdataserializer.readOptional(PacketDataSerializer::readComponent), packetdataserializer.readVarInt(), new ChatSender(packetdataserializer), packetdataserializer.readInstant(), new MinecraftEncryption.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.signedContent);
        packetdataserializer.writeOptional(this.unsignedContent, PacketDataSerializer::writeComponent);
        packetdataserializer.writeVarInt(this.typeId);
        this.sender.write(packetdataserializer);
        packetdataserializer.writeInstant(this.timeStamp);
        MinecraftEncryption.b.write(packetdataserializer, this.saltSignature);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public PlayerChatMessage getMessage() {
        MessageSignature messagesignature = new MessageSignature(this.sender.uuid(), this.timeStamp, this.saltSignature);

        return new PlayerChatMessage(this.signedContent, messagesignature, this.unsignedContent);
    }

    private Instant getExpiresAt() {
        return this.timeStamp.plus(ClientboundPlayerChatPacket.MESSAGE_EXPIRES_AFTER);
    }

    public boolean hasExpired(Instant instant) {
        return instant.isAfter(this.getExpiresAt());
    }

    public ChatMessageType resolveType(IRegistry<ChatMessageType> iregistry) {
        return (ChatMessageType) Objects.requireNonNull((ChatMessageType) iregistry.byId(this.typeId), "Invalid chat type");
    }
}

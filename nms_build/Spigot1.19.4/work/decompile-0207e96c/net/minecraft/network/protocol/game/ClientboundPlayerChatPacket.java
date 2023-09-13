package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatPacket(UUID sender, int index, @Nullable MessageSignature signature, SignedMessageBody.a body, @Nullable IChatBaseComponent unsignedContent, FilterMask filterMask, ChatMessageType.b chatType) implements Packet<PacketListenerPlayOut> {

    public ClientboundPlayerChatPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUUID(), packetdataserializer.readVarInt(), (MessageSignature) packetdataserializer.readNullable(MessageSignature::read), new SignedMessageBody.a(packetdataserializer), (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent), FilterMask.read(packetdataserializer), new ChatMessageType.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUUID(this.sender);
        packetdataserializer.writeVarInt(this.index);
        packetdataserializer.writeNullable(this.signature, MessageSignature::write);
        this.body.write(packetdataserializer);
        packetdataserializer.writeNullable(this.unsignedContent, PacketDataSerializer::writeComponent);
        FilterMask.write(packetdataserializer, this.filterMask);
        this.chatType.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}

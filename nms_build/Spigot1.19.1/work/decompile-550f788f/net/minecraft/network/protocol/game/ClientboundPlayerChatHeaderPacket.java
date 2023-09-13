package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageHeader;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatHeaderPacket(SignedMessageHeader header, MessageSignature headerSignature, byte[] bodyDigest) implements Packet<PacketListenerPlayOut> {

    public ClientboundPlayerChatHeaderPacket(PlayerChatMessage playerchatmessage) {
        this(playerchatmessage.signedHeader(), playerchatmessage.headerSignature(), playerchatmessage.signedBody().hash().asBytes());
    }

    public ClientboundPlayerChatHeaderPacket(PacketDataSerializer packetdataserializer) {
        this(new SignedMessageHeader(packetdataserializer), new MessageSignature(packetdataserializer), packetdataserializer.readByteArray());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        this.header.write(packetdataserializer);
        this.headerSignature.write(packetdataserializer);
        packetdataserializer.writeByteArray(this.bodyDigest);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlayerChatHeader(this);
    }
}

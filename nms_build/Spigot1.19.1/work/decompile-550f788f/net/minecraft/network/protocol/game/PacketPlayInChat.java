package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;

public record PacketPlayInChat(String message, Instant timeStamp, long salt, MessageSignature signature, boolean signedPreview, LastSeenMessages.b lastSeenMessages) implements Packet<PacketListenerPlayIn> {

    public PacketPlayInChat(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(256), packetdataserializer.readInstant(), packetdataserializer.readLong(), new MessageSignature(packetdataserializer), packetdataserializer.readBoolean(), new LastSeenMessages.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.message, 256);
        packetdataserializer.writeInstant(this.timeStamp);
        packetdataserializer.writeLong(this.salt);
        this.signature.write(packetdataserializer);
        packetdataserializer.writeBoolean(this.signedPreview);
        this.lastSeenMessages.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChat(this);
    }

    public MessageSigner getSigner(EntityPlayer entityplayer) {
        return new MessageSigner(entityplayer.getUUID(), this.timeStamp, this.salt);
    }
}

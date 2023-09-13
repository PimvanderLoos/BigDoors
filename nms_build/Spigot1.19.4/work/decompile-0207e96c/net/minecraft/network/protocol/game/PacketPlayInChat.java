package net.minecraft.network.protocol.game;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;

public record PacketPlayInChat(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.b lastSeenMessages) implements Packet<PacketListenerPlayIn> {

    public PacketPlayInChat(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(256), packetdataserializer.readInstant(), packetdataserializer.readLong(), (MessageSignature) packetdataserializer.readNullable(MessageSignature::read), new LastSeenMessages.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.message, 256);
        packetdataserializer.writeInstant(this.timeStamp);
        packetdataserializer.writeLong(this.salt);
        packetdataserializer.writeNullable(this.signature, MessageSignature::write);
        this.lastSeenMessages.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChat(this);
    }
}

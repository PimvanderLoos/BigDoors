package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.UtilColor;

public record ServerboundChatCommandPacket(String command, Instant timeStamp, long salt, ArgumentSignatures argumentSignatures, boolean signedPreview, LastSeenMessages.b lastSeenMessages) implements Packet<PacketListenerPlayIn> {

    public ServerboundChatCommandPacket(String s, Instant instant, long i, ArgumentSignatures argumentsignatures, boolean flag, LastSeenMessages.b lastseenmessages_b) {
        s = UtilColor.trimChatMessage(s);
        this.command = s;
        this.timeStamp = instant;
        this.salt = i;
        this.argumentSignatures = argumentsignatures;
        this.signedPreview = flag;
        this.lastSeenMessages = lastseenmessages_b;
    }

    public ServerboundChatCommandPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(256), packetdataserializer.readInstant(), packetdataserializer.readLong(), new ArgumentSignatures(packetdataserializer), packetdataserializer.readBoolean(), new LastSeenMessages.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.command, 256);
        packetdataserializer.writeInstant(this.timeStamp);
        packetdataserializer.writeLong(this.salt);
        this.argumentSignatures.write(packetdataserializer);
        packetdataserializer.writeBoolean(this.signedPreview);
        this.lastSeenMessages.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChatCommand(this);
    }
}

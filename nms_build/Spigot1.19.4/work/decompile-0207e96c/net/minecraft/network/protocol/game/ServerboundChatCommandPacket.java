package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;

public record ServerboundChatCommandPacket(String command, Instant timeStamp, long salt, ArgumentSignatures argumentSignatures, LastSeenMessages.b lastSeenMessages) implements Packet<PacketListenerPlayIn> {

    public ServerboundChatCommandPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(256), packetdataserializer.readInstant(), packetdataserializer.readLong(), new ArgumentSignatures(packetdataserializer), new LastSeenMessages.b(packetdataserializer));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.command, 256);
        packetdataserializer.writeInstant(this.timeStamp);
        packetdataserializer.writeLong(this.salt);
        this.argumentSignatures.write(packetdataserializer);
        this.lastSeenMessages.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChatCommand(this);
    }
}

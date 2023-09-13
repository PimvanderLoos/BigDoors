package net.minecraft.network.protocol.game;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.UtilColor;

public record ServerboundChatCommandPacket(String command, Instant timeStamp, ArgumentSignatures argumentSignatures, boolean signedPreview) implements Packet<PacketListenerPlayIn> {

    public ServerboundChatCommandPacket(String s, Instant instant, ArgumentSignatures argumentsignatures, boolean flag) {
        s = UtilColor.trimChatMessage(s);
        this.command = s;
        this.timeStamp = instant;
        this.argumentSignatures = argumentsignatures;
        this.signedPreview = flag;
    }

    public ServerboundChatCommandPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUtf(256), packetdataserializer.readInstant(), new ArgumentSignatures(packetdataserializer), packetdataserializer.readBoolean());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.command, 256);
        packetdataserializer.writeInstant(this.timeStamp);
        this.argumentSignatures.write(packetdataserializer);
        packetdataserializer.writeBoolean(this.signedPreview);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChatCommand(this);
    }

    public CommandSigningContext signingContext(UUID uuid) {
        return new CommandSigningContext.a(uuid, this.timeStamp, this.argumentSignatures, this.signedPreview);
    }
}

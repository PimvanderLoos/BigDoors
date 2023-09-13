package net.minecraft.network.protocol.game;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.util.UtilColor;

public class PacketPlayInChat implements Packet<PacketListenerPlayIn> {

    public static final Duration MESSAGE_EXPIRES_AFTER = Duration.ofMinutes(5L);
    private final String message;
    private final Instant timeStamp;
    private final MinecraftEncryption.b saltSignature;
    private final boolean signedPreview;

    public PacketPlayInChat(String s, MessageSignature messagesignature, boolean flag) {
        this.message = UtilColor.trimChatMessage(s);
        this.timeStamp = messagesignature.timeStamp();
        this.saltSignature = messagesignature.saltSignature();
        this.signedPreview = flag;
    }

    public PacketPlayInChat(PacketDataSerializer packetdataserializer) {
        this.message = packetdataserializer.readUtf(256);
        this.timeStamp = packetdataserializer.readInstant();
        this.saltSignature = new MinecraftEncryption.b(packetdataserializer);
        this.signedPreview = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.message, 256);
        packetdataserializer.writeInstant(this.timeStamp);
        MinecraftEncryption.b.write(packetdataserializer, this.saltSignature);
        packetdataserializer.writeBoolean(this.signedPreview);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChat(this);
    }

    public String getMessage() {
        return this.message;
    }

    public MessageSignature getSignature(UUID uuid) {
        return new MessageSignature(uuid, this.timeStamp, this.saltSignature);
    }

    public Instant getTimeStamp() {
        return this.timeStamp;
    }

    public boolean signedPreview() {
        return this.signedPreview;
    }
}

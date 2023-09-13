package net.minecraft.network.chat;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.SystemUtils;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.MinecraftEncryption;

public record MessageSigner(UUID profileId, Instant timeStamp, long salt) {

    public MessageSigner(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readUUID(), packetdataserializer.readInstant(), packetdataserializer.readLong());
    }

    public static MessageSigner create(UUID uuid) {
        return new MessageSigner(uuid, Instant.now(), MinecraftEncryption.c.getLong());
    }

    public static MessageSigner system() {
        return create(SystemUtils.NIL_UUID);
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUUID(this.profileId);
        packetdataserializer.writeInstant(this.timeStamp);
        packetdataserializer.writeLong(this.salt);
    }

    public boolean isSystem() {
        return this.profileId.equals(SystemUtils.NIL_UUID);
    }
}

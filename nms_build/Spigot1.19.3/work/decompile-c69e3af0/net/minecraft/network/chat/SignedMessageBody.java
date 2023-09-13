package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;

public record SignedMessageBody(String content, Instant timeStamp, long salt, LastSeenMessages lastSeen) {

    public static final MapCodec<SignedMessageBody> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.STRING.fieldOf("content").forGetter(SignedMessageBody::content), ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(SignedMessageBody::timeStamp), Codec.LONG.fieldOf("salt").forGetter(SignedMessageBody::salt), LastSeenMessages.CODEC.optionalFieldOf("last_seen", LastSeenMessages.EMPTY).forGetter(SignedMessageBody::lastSeen)).apply(instance, SignedMessageBody::new);
    });

    public static SignedMessageBody unsigned(String s) {
        return new SignedMessageBody(s, Instant.now(), 0L, LastSeenMessages.EMPTY);
    }

    public void updateSignature(SignatureUpdater.a signatureupdater_a) throws SignatureException {
        signatureupdater_a.update(Longs.toByteArray(this.salt));
        signatureupdater_a.update(Longs.toByteArray(this.timeStamp.getEpochSecond()));
        byte[] abyte = this.content.getBytes(StandardCharsets.UTF_8);

        signatureupdater_a.update(Ints.toByteArray(abyte.length));
        signatureupdater_a.update(abyte);
        this.lastSeen.updateSignature(signatureupdater_a);
    }

    public SignedMessageBody.a pack(MessageSignatureCache messagesignaturecache) {
        return new SignedMessageBody.a(this.content, this.timeStamp, this.salt, this.lastSeen.pack(messagesignaturecache));
    }

    public static record a(String content, Instant timeStamp, long salt, LastSeenMessages.a lastSeen) {

        public a(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readUtf(256), packetdataserializer.readInstant(), packetdataserializer.readLong(), new LastSeenMessages.a(packetdataserializer));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeUtf(this.content, 256);
            packetdataserializer.writeInstant(this.timeStamp);
            packetdataserializer.writeLong(this.salt);
            this.lastSeen.write(packetdataserializer);
        }

        public Optional<SignedMessageBody> unpack(MessageSignatureCache messagesignaturecache) {
            return this.lastSeen.unpack(messagesignaturecache).map((lastseenmessages) -> {
                return new SignedMessageBody(this.content, this.timeStamp, this.salt, lastseenmessages);
            });
        }
    }
}

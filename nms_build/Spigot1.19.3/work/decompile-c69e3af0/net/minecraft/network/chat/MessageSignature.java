package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(byte[] bytes) {

    public static final Codec<MessageSignature> CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
    public static final int BYTES = 256;

    public MessageSignature(byte[] abyte) {
        Preconditions.checkState(abyte.length == 256, "Invalid message signature size");
        this.bytes = abyte;
    }

    public static MessageSignature read(PacketDataSerializer packetdataserializer) {
        byte[] abyte = new byte[256];

        packetdataserializer.readBytes(abyte);
        return new MessageSignature(abyte);
    }

    public static void write(PacketDataSerializer packetdataserializer, MessageSignature messagesignature) {
        packetdataserializer.writeBytes(messagesignature.bytes);
    }

    public boolean verify(SignatureValidator signaturevalidator, SignatureUpdater signatureupdater) {
        return signaturevalidator.validate(signatureupdater, this.bytes);
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.bytes);
    }

    public boolean equals(Object object) {
        boolean flag;

        if (this != object) {
            label22:
            {
                if (object instanceof MessageSignature) {
                    MessageSignature messagesignature = (MessageSignature) object;

                    if (Arrays.equals(this.bytes, messagesignature.bytes)) {
                        break label22;
                    }
                }

                flag = false;
                return flag;
            }
        }

        flag = true;
        return flag;
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return Base64.getEncoder().encodeToString(this.bytes);
    }

    public MessageSignature.a pack(MessageSignatureCache messagesignaturecache) {
        int i = messagesignaturecache.pack(this);

        return i != -1 ? new MessageSignature.a(i) : new MessageSignature.a(this);
    }

    public static record a(int id, @Nullable MessageSignature fullSignature) {

        public static final int FULL_SIGNATURE = -1;

        public a(MessageSignature messagesignature) {
            this(-1, messagesignature);
        }

        public a(int i) {
            this(i, (MessageSignature) null);
        }

        public static MessageSignature.a read(PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.readVarInt() - 1;

            return i == -1 ? new MessageSignature.a(MessageSignature.read(packetdataserializer)) : new MessageSignature.a(i);
        }

        public static void write(PacketDataSerializer packetdataserializer, MessageSignature.a messagesignature_a) {
            packetdataserializer.writeVarInt(messagesignature_a.id() + 1);
            if (messagesignature_a.fullSignature() != null) {
                MessageSignature.write(packetdataserializer, messagesignature_a.fullSignature());
            }

        }

        public Optional<MessageSignature> unpack(MessageSignatureCache messagesignaturecache) {
            return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(messagesignaturecache.unpack(this.id));
        }
    }
}

package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.util.SignatureValidator;

public record ProfilePublicKey(ProfilePublicKey.a data) {

    public static final IChatBaseComponent EXPIRED_PROFILE_PUBLIC_KEY = IChatBaseComponent.translatable("multiplayer.disconnect.expired_public_key");
    private static final IChatBaseComponent INVALID_SIGNATURE = IChatBaseComponent.translatable("multiplayer.disconnect.invalid_public_key_signature");
    public static final Duration EXPIRY_GRACE_PERIOD = Duration.ofHours(8L);
    public static final Codec<ProfilePublicKey> TRUSTED_CODEC = ProfilePublicKey.a.CODEC.xmap(ProfilePublicKey::new, ProfilePublicKey::data);

    public static ProfilePublicKey createValidated(SignatureValidator signaturevalidator, UUID uuid, ProfilePublicKey.a profilepublickey_a, Duration duration) throws ProfilePublicKey.b {
        if (profilepublickey_a.hasExpired(duration)) {
            throw new ProfilePublicKey.b(ProfilePublicKey.EXPIRED_PROFILE_PUBLIC_KEY);
        } else if (!profilepublickey_a.validateSignature(signaturevalidator, uuid)) {
            throw new ProfilePublicKey.b(ProfilePublicKey.INVALID_SIGNATURE);
        } else {
            return new ProfilePublicKey(profilepublickey_a);
        }
    }

    public SignatureValidator createSignatureValidator() {
        return SignatureValidator.from(this.data.key, "SHA256withRSA");
    }

    public static record a(Instant expiresAt, PublicKey key, byte[] keySignature) {

        private static final int MAX_KEY_SIGNATURE_SIZE = 4096;
        public static final Codec<ProfilePublicKey.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(ProfilePublicKey.a::expiresAt), MinecraftEncryption.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(ProfilePublicKey.a::key), ExtraCodecs.BASE64_STRING.fieldOf("signature_v2").forGetter(ProfilePublicKey.a::keySignature)).apply(instance, ProfilePublicKey.a::new);
        });

        public a(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readInstant(), packetdataserializer.readPublicKey(), packetdataserializer.readByteArray(4096));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeInstant(this.expiresAt);
            packetdataserializer.writePublicKey(this.key);
            packetdataserializer.writeByteArray(this.keySignature);
        }

        boolean validateSignature(SignatureValidator signaturevalidator, UUID uuid) {
            return signaturevalidator.validate(this.signedPayload(uuid), this.keySignature);
        }

        private byte[] signedPayload(UUID uuid) {
            byte[] abyte = this.key.getEncoded();
            byte[] abyte1 = new byte[24 + abyte.length];
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte1).order(ByteOrder.BIG_ENDIAN);

            bytebuffer.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).putLong(this.expiresAt.toEpochMilli()).put(abyte);
            return abyte1;
        }

        public boolean hasExpired() {
            return this.expiresAt.isBefore(Instant.now());
        }

        public boolean hasExpired(Duration duration) {
            return this.expiresAt.plus(duration).isBefore(Instant.now());
        }

        public boolean equals(Object object) {
            if (!(object instanceof ProfilePublicKey.a)) {
                return false;
            } else {
                ProfilePublicKey.a profilepublickey_a = (ProfilePublicKey.a) object;

                return this.expiresAt.equals(profilepublickey_a.expiresAt) && this.key.equals(profilepublickey_a.key) && Arrays.equals(this.keySignature, profilepublickey_a.keySignature);
            }
        }
    }

    public static class b extends ThrowingComponent {

        public b(IChatBaseComponent ichatbasecomponent) {
            super(ichatbasecomponent);
        }
    }
}

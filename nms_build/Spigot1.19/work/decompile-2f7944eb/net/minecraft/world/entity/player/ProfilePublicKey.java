package net.minecraft.world.entity.player;

import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.InvalidException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.util.SignatureValidator;

public record ProfilePublicKey(ProfilePublicKey.a data) {

    public static final Codec<ProfilePublicKey> TRUSTED_CODEC = ProfilePublicKey.a.CODEC.comapFlatMap((profilepublickey_a) -> {
        try {
            return DataResult.success(createTrusted(profilepublickey_a));
        } catch (CryptographyException cryptographyexception) {
            return DataResult.error("Malformed public key");
        }
    }, ProfilePublicKey::data);

    public static ProfilePublicKey createTrusted(ProfilePublicKey.a profilepublickey_a) throws CryptographyException {
        return new ProfilePublicKey(profilepublickey_a);
    }

    public static ProfilePublicKey createValidated(SignatureValidator signaturevalidator, ProfilePublicKey.a profilepublickey_a) throws InsecurePublicKeyException, CryptographyException {
        if (profilepublickey_a.hasExpired()) {
            throw new InvalidException("Expired profile public key");
        } else if (!profilepublickey_a.validateSignature(signaturevalidator)) {
            throw new InvalidException("Invalid profile public key signature");
        } else {
            return createTrusted(profilepublickey_a);
        }
    }

    public SignatureValidator createSignatureValidator() {
        return SignatureValidator.from(this.data.key, "SHA256withRSA");
    }

    public static record a(Instant expiresAt, PublicKey key, byte[] keySignature) {

        private static final int MAX_KEY_SIGNATURE_SIZE = 4096;
        public static final Codec<ProfilePublicKey.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(ProfilePublicKey.a::expiresAt), MinecraftEncryption.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(ProfilePublicKey.a::key), ExtraCodecs.BASE64_STRING.fieldOf("signature").forGetter(ProfilePublicKey.a::keySignature)).apply(instance, ProfilePublicKey.a::new);
        });

        public a(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readInstant(), packetdataserializer.readPublicKey(), packetdataserializer.readByteArray(4096));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeInstant(this.expiresAt);
            packetdataserializer.writePublicKey(this.key);
            packetdataserializer.writeByteArray(this.keySignature);
        }

        boolean validateSignature(SignatureValidator signaturevalidator) {
            return signaturevalidator.validate(this.signedPayload().getBytes(StandardCharsets.US_ASCII), this.keySignature);
        }

        private String signedPayload() {
            String s = MinecraftEncryption.rsaPublicKeyToString(this.key);
            long i = this.expiresAt.toEpochMilli();

            return i + s;
        }

        public boolean hasExpired() {
            return this.expiresAt.isBefore(Instant.now());
        }
    }
}

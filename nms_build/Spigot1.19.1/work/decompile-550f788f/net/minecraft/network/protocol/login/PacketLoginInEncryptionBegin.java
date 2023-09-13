package net.minecraft.network.protocol.login;

import com.mojang.datafixers.util.Either;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.SecretKey;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.MinecraftEncryption;
import net.minecraft.world.entity.player.ProfilePublicKey;

public class PacketLoginInEncryptionBegin implements Packet<PacketLoginInListener> {

    private final byte[] keybytes;
    private final Either<byte[], MinecraftEncryption.b> nonceOrSaltSignature;

    public PacketLoginInEncryptionBegin(SecretKey secretkey, PublicKey publickey, byte[] abyte) throws CryptographyException {
        this.keybytes = MinecraftEncryption.encryptUsingKey(publickey, secretkey.getEncoded());
        this.nonceOrSaltSignature = Either.left(MinecraftEncryption.encryptUsingKey(publickey, abyte));
    }

    public PacketLoginInEncryptionBegin(SecretKey secretkey, PublicKey publickey, long i, byte[] abyte) throws CryptographyException {
        this.keybytes = MinecraftEncryption.encryptUsingKey(publickey, secretkey.getEncoded());
        this.nonceOrSaltSignature = Either.right(new MinecraftEncryption.b(i, abyte));
    }

    public PacketLoginInEncryptionBegin(PacketDataSerializer packetdataserializer) {
        this.keybytes = packetdataserializer.readByteArray();
        this.nonceOrSaltSignature = packetdataserializer.readEither(PacketDataSerializer::readByteArray, MinecraftEncryption.b::new);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByteArray(this.keybytes);
        packetdataserializer.writeEither(this.nonceOrSaltSignature, PacketDataSerializer::writeByteArray, MinecraftEncryption.b::write);
    }

    public void handle(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.handleKey(this);
    }

    public SecretKey getSecretKey(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.decryptByteToSecretKey(privatekey, this.keybytes);
    }

    public boolean isChallengeSignatureValid(byte[] abyte, ProfilePublicKey profilepublickey) {
        return (Boolean) this.nonceOrSaltSignature.map((abyte1) -> {
            return false;
        }, (minecraftencryption_b) -> {
            return profilepublickey.createSignatureValidator().validate((signatureupdater_a) -> {
                signatureupdater_a.update(abyte);
                signatureupdater_a.update(minecraftencryption_b.saltAsBytes());
            }, minecraftencryption_b.signature());
        });
    }

    public boolean isNonceValid(byte[] abyte, PrivateKey privatekey) {
        Optional optional = this.nonceOrSaltSignature.left();

        try {
            return optional.isPresent() && Arrays.equals(abyte, MinecraftEncryption.decryptUsingKey(privatekey, (byte[]) optional.get()));
        } catch (CryptographyException cryptographyexception) {
            return false;
        }
    }
}

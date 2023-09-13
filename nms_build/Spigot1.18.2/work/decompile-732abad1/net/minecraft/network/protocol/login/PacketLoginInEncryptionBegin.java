package net.minecraft.network.protocol.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.MinecraftEncryption;

public class PacketLoginInEncryptionBegin implements Packet<PacketLoginInListener> {

    private final byte[] keybytes;
    private final byte[] nonce;

    public PacketLoginInEncryptionBegin(SecretKey secretkey, PublicKey publickey, byte[] abyte) throws CryptographyException {
        this.keybytes = MinecraftEncryption.encryptUsingKey(publickey, secretkey.getEncoded());
        this.nonce = MinecraftEncryption.encryptUsingKey(publickey, abyte);
    }

    public PacketLoginInEncryptionBegin(PacketDataSerializer packetdataserializer) {
        this.keybytes = packetdataserializer.readByteArray();
        this.nonce = packetdataserializer.readByteArray();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByteArray(this.keybytes);
        packetdataserializer.writeByteArray(this.nonce);
    }

    public void handle(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.handleKey(this);
    }

    public SecretKey getSecretKey(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.decryptByteToSecretKey(privatekey, this.keybytes);
    }

    public byte[] getNonce(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.decryptUsingKey(privatekey, this.nonce);
    }
}

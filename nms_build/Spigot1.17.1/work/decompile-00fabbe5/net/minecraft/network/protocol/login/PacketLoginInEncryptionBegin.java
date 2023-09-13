package net.minecraft.network.protocol.login;

import java.security.Key;
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
        this.keybytes = MinecraftEncryption.a((Key) publickey, secretkey.getEncoded());
        this.nonce = MinecraftEncryption.a((Key) publickey, abyte);
    }

    public PacketLoginInEncryptionBegin(PacketDataSerializer packetdataserializer) {
        this.keybytes = packetdataserializer.b();
        this.nonce = packetdataserializer.b();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.keybytes);
        packetdataserializer.a(this.nonce);
    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }

    public SecretKey a(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.a(privatekey, this.keybytes);
    }

    public byte[] b(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.b(privatekey, this.nonce);
    }
}

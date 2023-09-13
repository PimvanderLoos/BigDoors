package net.minecraft.network.protocol.login;

import java.security.PublicKey;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.CryptographyException;
import net.minecraft.util.MinecraftEncryption;

public class PacketLoginOutEncryptionBegin implements Packet<PacketLoginOutListener> {

    private final String serverId;
    private final byte[] publicKey;
    private final byte[] nonce;

    public PacketLoginOutEncryptionBegin(String s, byte[] abyte, byte[] abyte1) {
        this.serverId = s;
        this.publicKey = abyte;
        this.nonce = abyte1;
    }

    public PacketLoginOutEncryptionBegin(PacketDataSerializer packetdataserializer) {
        this.serverId = packetdataserializer.readUtf(20);
        this.publicKey = packetdataserializer.readByteArray();
        this.nonce = packetdataserializer.readByteArray();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeUtf(this.serverId);
        packetdataserializer.writeByteArray(this.publicKey);
        packetdataserializer.writeByteArray(this.nonce);
    }

    public void handle(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.handleHello(this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() throws CryptographyException {
        return MinecraftEncryption.byteToPublicKey(this.publicKey);
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}

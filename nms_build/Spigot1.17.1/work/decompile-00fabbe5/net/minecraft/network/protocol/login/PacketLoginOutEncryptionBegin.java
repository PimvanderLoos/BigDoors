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
        this.serverId = packetdataserializer.e(20);
        this.publicKey = packetdataserializer.b();
        this.nonce = packetdataserializer.b();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.serverId);
        packetdataserializer.a(this.publicKey);
        packetdataserializer.a(this.nonce);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }

    public String b() {
        return this.serverId;
    }

    public PublicKey c() throws CryptographyException {
        return MinecraftEncryption.a(this.publicKey);
    }

    public byte[] d() {
        return this.nonce;
    }
}

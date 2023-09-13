package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginOutEncryptionBegin implements Packet<PacketLoginOutListener> {

    private String a;
    private byte[] b;
    private byte[] c;

    public PacketLoginOutEncryptionBegin() {}

    public PacketLoginOutEncryptionBegin(String s, byte[] abyte, byte[] abyte1) {
        this.a = s;
        this.b = abyte;
        this.c = abyte1;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e(20);
        this.b = packetdataserializer.a();
        this.c = packetdataserializer.a();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.a(this.c);
    }

    public void a(PacketLoginOutListener packetloginoutlistener) {
        packetloginoutlistener.a(this);
    }
}

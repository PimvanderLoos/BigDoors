package net.minecraft.network.protocol.handshake;

import net.minecraft.SharedConstants;
import net.minecraft.network.EnumProtocol;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketHandshakingInSetProtocol implements Packet<PacketHandshakingInListener> {

    private static final int MAX_HOST_LENGTH = 255;
    private final int protocolVersion;
    public String hostName;
    public final int port;
    private final EnumProtocol intention;

    public PacketHandshakingInSetProtocol(String s, int i, EnumProtocol enumprotocol) {
        this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
        this.hostName = s;
        this.port = i;
        this.intention = enumprotocol;
    }

    public PacketHandshakingInSetProtocol(PacketDataSerializer packetdataserializer) {
        this.protocolVersion = packetdataserializer.j();
        this.hostName = packetdataserializer.e(255);
        this.port = packetdataserializer.readUnsignedShort();
        this.intention = EnumProtocol.a(packetdataserializer.j());
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.protocolVersion);
        packetdataserializer.a(this.hostName);
        packetdataserializer.writeShort(this.port);
        packetdataserializer.d(this.intention.a());
    }

    public void a(PacketHandshakingInListener packethandshakinginlistener) {
        packethandshakinginlistener.a(this);
    }

    public EnumProtocol b() {
        return this.intention;
    }

    public int c() {
        return this.protocolVersion;
    }

    public String d() {
        return this.hostName;
    }

    public int e() {
        return this.port;
    }
}

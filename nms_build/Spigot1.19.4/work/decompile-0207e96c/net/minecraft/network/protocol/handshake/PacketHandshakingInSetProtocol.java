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
        this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
        this.hostName = s;
        this.port = i;
        this.intention = enumprotocol;
    }

    public PacketHandshakingInSetProtocol(PacketDataSerializer packetdataserializer) {
        this.protocolVersion = packetdataserializer.readVarInt();
        this.hostName = packetdataserializer.readUtf(255);
        this.port = packetdataserializer.readUnsignedShort();
        this.intention = EnumProtocol.getById(packetdataserializer.readVarInt());
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.protocolVersion);
        packetdataserializer.writeUtf(this.hostName);
        packetdataserializer.writeShort(this.port);
        packetdataserializer.writeVarInt(this.intention.getId());
    }

    public void handle(PacketHandshakingInListener packethandshakinginlistener) {
        packethandshakinginlistener.handleIntention(this);
    }

    public EnumProtocol getIntention() {
        return this.intention;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPort() {
        return this.port;
    }
}

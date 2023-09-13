package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public record PacketStatusOutServerInfo(ServerPing status) implements Packet<PacketStatusOutListener> {

    public PacketStatusOutServerInfo(PacketDataSerializer packetdataserializer) {
        this((ServerPing) packetdataserializer.readJsonWithCodec(ServerPing.CODEC));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeJsonWithCodec(ServerPing.CODEC, this.status);
    }

    public void handle(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.handleStatusResponse(this);
    }
}

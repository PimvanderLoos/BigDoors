package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.UtilColor;

public record ServerboundChatPreviewPacket(int queryId, String query) implements Packet<PacketListenerPlayIn> {

    public ServerboundChatPreviewPacket(int i, String s) {
        s = UtilColor.trimChatMessage(s);
        this.queryId = i;
        this.query = s;
    }

    public ServerboundChatPreviewPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readInt(), packetdataserializer.readUtf(256));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.queryId);
        packetdataserializer.writeUtf(this.query, 256);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleChatPreview(this);
    }
}

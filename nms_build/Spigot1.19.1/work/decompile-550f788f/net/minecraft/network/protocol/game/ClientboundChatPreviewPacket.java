package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;

public record ClientboundChatPreviewPacket(int queryId, @Nullable IChatBaseComponent preview) implements Packet<PacketListenerPlayOut> {

    public ClientboundChatPreviewPacket(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readInt(), (IChatBaseComponent) packetdataserializer.readNullable(PacketDataSerializer::readComponent));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.queryId);
        packetdataserializer.writeNullable(this.preview, PacketDataSerializer::writeComponent);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleChatPreview(this);
    }
}
